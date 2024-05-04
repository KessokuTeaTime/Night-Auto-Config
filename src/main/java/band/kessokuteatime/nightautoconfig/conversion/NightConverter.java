package band.kessokuteatime.nightautoconfig.conversion;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.conversion.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;

public class NightConverter {
    private final boolean bypassTransient, bypassFinal;

    public NightConverter(boolean bypassTransient, boolean bypassFinal) {
        this.bypassTransient = bypassTransient;
        this.bypassFinal = bypassFinal;
    }

    public NightConverter() {
        this(true, true);
    }

    public void toConfig(Object o, Config destination) {
        Objects.requireNonNull(o, "The object must not be null.");
        Objects.requireNonNull(destination, "The config must not be null.");
        Class<?> clazz = o.getClass();
        List<String> annotatedPath = AnnotationUtils.getPath(clazz);
        if (annotatedPath != null) {
            destination = destination.getRaw(annotatedPath);
        }
        convertToConfig(o, clazz, destination);
    }

    public void toConfig(Class<?> clazz, Config destination) {
        Objects.requireNonNull(destination, "The config must not be null.");
        List<String> annotatedPath = AnnotationUtils.getPath(clazz);
        if (annotatedPath != null) {
            destination = destination.getRaw(annotatedPath);
        }
        convertToConfig(null, clazz, destination);
    }

    /**
     * Converts an Object to a Config.
     *
     * @param o                   the object to convert
     * @param destinationSupplier a Supplier that provides the Config where to put the values into
     * @param <C>                 the destination's type
     * @return the Config obtained from the Supplier
     */
    public <C extends Config> C toConfig(Object o, Supplier<C> destinationSupplier) {
        C destination = destinationSupplier.get();
        toConfig(o, destination);
        return destination;
    }

    public <C extends Config> C toConfig(Class<?> clazz, Supplier<C> destinationSupplier) {
        C destination = destinationSupplier.get();
        toConfig(clazz, destination);
        return destination;
    }

    /**
     * Converts a Config to an Object.
     *
     * @param config      the config to convert
     * @param destination the Object where to put the values into
     */
    public void toObject(UnmodifiableConfig config, Object destination) {
        Objects.requireNonNull(config, "The config must not be null.");
        Objects.requireNonNull(destination, "The object must not be null.");
        Class<?> clazz = destination.getClass();
        List<String> annotatedPath = AnnotationUtils.getPath(clazz);
        if (annotatedPath != null) {
            config = config.getRaw(annotatedPath);
        }
        convertToObject(config, destination, clazz);
    }

    /**
     * Converts a Config to an Object.
     *
     * @param config              the config to convert
     * @param destinationSupplier a Supplier that provides the Object where to put the values into
     * @param <O>                 the destination's type
     * @return the object obtained from the Supplier
     */
    public <O> O toObject(UnmodifiableConfig config, Supplier<O> destinationSupplier) {
        O destination = destinationSupplier.get();
        toObject(config, destination);
        return destination;
    }

    private boolean supportsType(ConfigFormat<?> format, Class<?> type) {
        return format.supportsType(type) || Map.class.isAssignableFrom(type);
    }

    /**
     * Converts an Object to a Config. The {@link #bypassTransient} setting applies.
     */
    private void convertToConfig(Object object, Class<?> clazz, Config destination) {
        // This loop walks through the class hierarchy, see clazz = clazz.getSuperclass(); at the end
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                // --- Checks modifiers ---
                final int fieldModifiers = field.getModifiers();

                if (object != null && Modifier.isStatic(fieldModifiers)) {
                    continue;
                }

                if (bypassFinal && Modifier.isFinal(fieldModifiers)) {
                    continue;
                }

                if (bypassTransient && Modifier.isTransient(fieldModifiers)) {
                    continue;
                }

                System.out.println(field);
                field.setAccessible(true);

                // --- Applies annotations ---
                Object value;
                try {
                    value = field.get(object);
                } catch (IllegalAccessException e) {
                    throw new ReflectionException("Unable to parse the field " + field, e);
                }

                Optional<Converter<Object, Object>> converter = AnnotationUtils.getConverter(object, field);
                if (converter.isPresent()) {
                    value = converter.get().convertFromField(value);
                }

                List<String> path = AnnotationUtils.getPath(field);
                ConfigFormat<?> format = destination.configFormat();

                // --- Writes the value to the configuration ---
                if (value == null) {
                    destination.set(path, null);
                } else {
                    Class<?> valueType = value.getClass();
                    if (Enum.class.isAssignableFrom(valueType)) {
                        // Enums must not be treated as objects to break down
                        // Note: isEnum() doesn't work with enum items that have a body
                        if (destination.configFormat().supportsType(Enum.class)) {
                            destination.set(path, value); // keep the enum value if supported
                        } else {
                            destination.set(path, value.toString()); // if not supported, serialize it
                        }
                    } else if (field.isAnnotationPresent(ForceBreakdown.class) || !supportsType(format, valueType)) {
                        // We have to convert the value
                        destination.set(path, value);
                        Config converted = destination.createSubConfig();
                        convertToConfig(value, valueType, converted);
                        destination.set(path, converted);
                    } else if (value instanceof Collection<?> src) {
                        // Checks that the ConfigFormat supports the type of the collection's elements
                        Class<?> bottomType = bottomElementType(src);
                        if (supportsType(format, bottomType)) {
                            // Everything is supported, no conversion needed
                            destination.set(path, value);
                        } else {
                            // List of complex objects => the bottom elements need conversion
                            Collection<Object> dst = new ArrayList<>(src.size());
                            convertObjectsToConfigs(src, bottomType, dst, destination);
                            destination.set(path, dst);
                        }
                    } else if (value instanceof Map<?, ?> src) {
                        // Checks that the ConfigFormat supports the type of the map's keys and values
                        Class<?> kType = bottomElementType(src.keySet());
                        Class<?> vType = bottomElementType(src.values());
                        if (supportsType(format, kType) && supportsType(format, vType)) {
                            // Everything is supported, no conversion needed
                            destination.set(path, value);
                        } else {
                            // Map of complex objects => the bottom elements need conversion
                            Map<Object, Object> dst = new HashMap<>(src.size());
                            convertObjectsToConfigs(src.keySet(), kType, dst.keySet(), destination);
                            convertObjectsToConfigs(src.values(), vType, dst.values(), destination);
                            destination.set(path, dst);
                        }
                    }else {
                        // Simple value
                        destination.set(path, value);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Converts a Config to an Object. The {@link #bypassTransient} and {@link #bypassFinal}
     * settings apply.
     */
    private void convertToObject(UnmodifiableConfig config, Object object, Class<?> clazz) {
        System.out.println(object + ", " + clazz);
        // This loop walks through the class hierarchy, see clazz = clazz.getSuperclass(); at the end
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                // --- Checks modifiers ---
                final int fieldModifiers = field.getModifiers();

                if (object != null && Modifier.isStatic(fieldModifiers)) {
                    continue;
                }

                if (bypassFinal && Modifier.isFinal(fieldModifiers)) {
                    continue;
                }

                if (bypassTransient && Modifier.isTransient(fieldModifiers)) {
                    continue;
                }

                field.setAccessible(true);

                // --- Applies annotations ---
                List<String> path = AnnotationUtils.getPath(field);
                Object value = config.get(path);

                Optional<Converter<Object, Object>> converter = AnnotationUtils.getConverter(object, field);
                if (converter.isPresent()) {
                    value = converter.get().convertToField(value);
                }

                // --- Writes the value to the object's field, converting it if needed ---
                Class<?> fieldType = field.getType();
                try {
                    if (value instanceof UnmodifiableConfig cfg && !(fieldType.isAssignableFrom(value.getClass()))) {
                        // --- Read as a sub-object ---

                        if (Map.class.isAssignableFrom(fieldType)) {
                            // --- Reads as a map, maybe a map of objects with conversion ---
                            final Class<?> srcVType = bottomElementType(cfg.valueMap().values());

                            final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                            final Class<?> dstKType = bottomElementType(genericType.getActualTypeArguments()[0]);
                            final Class<?> dstVType = bottomElementType(genericType.getActualTypeArguments()[1]);

                            // Map of objects => the bottom elements need conversion

                            // Uses the current field value if there is one, or create a new map
                            Map<Object, Object> dst = (Map<Object, Object>) field.get(object);
                            if (dst == null) {
                                if (fieldType == HashMap.class
                                        || fieldType.isInterface()
                                        || Modifier.isAbstract(fieldType.getModifiers())) {
                                    dst = new HashMap<>(cfg.valueMap().size()); // allocates the right size
                                } else {
                                    dst = (Map<Object, Object>) createInstance(fieldType);
                                }
                                field.set(object, dst);
                            }

                            // Converts the keys and values of the map
                            convertConfigsToObject(cfg.valueMap(), dst, (Class<Map<?, ?>>) fieldType);
                        } else {
                            // Gets or creates the field and convert it (if null OR not preserved)
                            Object fieldValue = field.get(object);
                            if (fieldValue == null) {
                                fieldValue = createInstance(fieldType);
                                field.set(object, fieldValue);
                                convertToObject(cfg, fieldValue, field.getType());
                            } else if (!AnnotationUtils.mustPreserve(field, clazz)) {
                                System.out.println("Current: " + field);
                                convertToObject(cfg, fieldValue, field.getType());
                            }
                        }
                    } else if (value instanceof Collection<?> src && Collection.class.isAssignableFrom(fieldType)) {
                        // --- Reads as a collection, maybe a list of objects with conversion ---
                        final Class<?> srcBottomType = bottomElementType(src);

                        final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                        final List<Class<?>> dstTypes = elementTypes(genericType);
                        final Class<?> dstBottomType = dstTypes.get(dstTypes.size() - 1);

                        if (srcBottomType == null
                                || dstBottomType == null
                                || dstBottomType.isAssignableFrom(srcBottomType)) {

                            // Simple list, no conversion needed
                            field.set(object, value);
                        } else {
                            // List of objects => the bottom elements need conversion

                            // Uses the current field value if there is one, or create a new list
                            Collection<Object> dst = (Collection<Object>) field.get(object);
                            if (dst == null) {
                                if (fieldType == ArrayList.class
                                        || fieldType.isInterface()
                                        || Modifier.isAbstract(fieldType.getModifiers())) {
                                    dst = new ArrayList<>(src.size()); // allocates the right size
                                } else {
                                    dst = (Collection<Object>) createInstance(fieldType);
                                }
                                field.set(object, dst);
                            }

                            // Converts the elements of the list
                            convertConfigsToObject(src, dst, dstTypes, 0);
                        }
                    } else {
                        if (field.getType().isEnum()) {
                            setEnumField(field, object, value);
                        } else {
                            field.set(object, value);
                        }
                    }
                } catch (ReflectiveOperationException ex) {
                    throw new ReflectionException("Unable to work with field " + field, ex);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private <E extends Enum<E>> void setEnumField(Field field, Object object, Object value) throws IllegalAccessException {
        Class<E> enumType = (Class<E>) field.getType();
        SpecEnum specEnum = field.getAnnotation(SpecEnum.class);
        EnumGetMethod method = (specEnum == null) ? EnumGetMethod.NAME_IGNORECASE : specEnum.method();
        field.set(object, method.get(value, enumType));
    }

    /**
     * Gets the type of the "bottom element" of a list.
     * For instance, for {@code LinkedList<List<List<Supplier<String>>>>}
     * this method returns the class {@code Supplier}.
     *
     * @param genericType the generic list type
     * @return the type of the elements of the most nested list
     */
    private Class<?> bottomElementType(ParameterizedType genericType) {
        if (genericType != null && genericType.getActualTypeArguments().length > 0) {
            Type parameter = genericType.getActualTypeArguments()[0];
            if (parameter instanceof ParameterizedType genericParameter) {
                Class<?> paramClass = (Class<?>) genericParameter.getRawType();
                if (paramClass.isAssignableFrom(Collection.class)) {
                    return bottomElementType(genericParameter);
                } else {
                    return paramClass;
                }
            }
            if ((parameter instanceof Class)) {
                return (Class<?>)parameter;
            }
        }
        return null;
    }

    private Class<?> bottomElementType(Type type) {
        if (type instanceof ParameterizedType genericType) {
            return bottomElementType(genericType);
        } else {
            return (Class<?>)type;
        }
    }

    private void detectElementTypes(ParameterizedType genericType, List<Class<?>> storage) {
        if (genericType != null && genericType.getActualTypeArguments().length > 0) {
            Type parameter = genericType.getActualTypeArguments()[0];
            if (parameter instanceof ParameterizedType genericParameter) {
                Class<?> paramClass = (Class<?>) genericParameter.getRawType();

                storage.add(paramClass);
                if (Collection.class.isAssignableFrom(paramClass)) {
                    detectElementTypes(genericParameter, storage);
                }
            } else if ((parameter instanceof Class)) {
                storage.add((Class<?>)parameter);
            }
        }
    }

    /**
     * Returns a list of the generic parameters of a list.
     * For instance, for {@code LinkedList<List<Collection<Supplier<String>>>>}
     * this method returns a list containing {@code [Collection.class, Supplier.class]}.
     *
     * @param genericType the list generic type
     * @return a list of the types of the list's elements
     */
    private List<Class<?>> elementTypes(ParameterizedType genericType) {
        List<Class<?>> storage = new ArrayList<>();
        detectElementTypes(genericType, storage);
        return storage;
    }

    /**
     * Gets the type of the "bottom element" of a collection.
     * For instance, for a list {@code [["string"], ["another string"]]}
     * this method returns the class {@code String}.
     *
     * @param list the list object
     * @return the type of the elements of the most nested list
     */
    private Class<?> bottomElementType(Collection<?> list) {
        for (Object element : list) {
            if (element instanceof Collection) {
                return bottomElementType((Collection<?>)element);
            } else if (element != null) {
                return element.getClass();
            }
        }
        return null;
    }

    /**
     * Converts a collection of configurations to a collection of objects of the type dstBottomType.
     *
     * @param src             the collection of configs, may be nested, source
     * @param dst             the collection of objects, destination
     * @param dstElementTypes the type of lists and objects in dst
     */
    private void convertConfigsToObject(
            Collection<?> src, Collection<Object> dst,
            List<Class<?>> dstElementTypes,
            int currentLevel
    ) {
        final Class<?> currentType = dstElementTypes.get(currentLevel);
        for (Object element : src) {
            if (element == null) {
                dst.add(null);
            } else if (element instanceof Collection<?> subSrc) {
                final Collection<Object> subDst;

                if (currentType == ArrayList.class
                        || currentType.isInterface()
                        || Modifier.isAbstract(currentType.getModifiers())) {

                    subDst = new ArrayList<>();
                } else {
                    subDst = (Collection<Object>) createInstance(currentType);
                }
                convertConfigsToObject(subSrc, subDst, dstElementTypes, currentLevel + 1);

                dst.add(subDst);
            } else if (element instanceof UnmodifiableConfig) {
                Object elementObj = createInstance(currentType);
                convertToObject((UnmodifiableConfig) element, elementObj, currentType);

                dst.add(elementObj);
            } else {
                String elementType = element.getClass().toString();
                throw new InvalidValueException("Unexpected element of type " + elementType + " in collection of objects");
            }
        }
    }

    private <K, V> void convertConfigsToObject(
            Map<String, Object> src, Map<K, V> dst,
            Class<Map<K, V>> mapType
    ) {
        for (Map.Entry<String, Object> entry : src.entrySet()) {
            K key = to(entry.getKey());
            Object value = entry.getValue();

            if (value == null) {
                dst.put(key, null);
            }

            else if (value instanceof Map) {
                Map<?, ?> subDst;

                if (mapType.isInterface()
                        || Modifier.isAbstract(mapType.getModifiers())) {

                    subDst = new HashMap<>();
                } else {
                    subDst = createInstance(mapType);
                }
                convertConfigsToObject(value, subDst, mapType);

                dst.put(key, subDst);
            } else if (value instanceof UnmodifiableConfig) {
                Object elementObj = createInstance(mapType);
                convertToObject((UnmodifiableConfig)value, elementObj, mapType);

                dst.put(key, elementObj);
            } else {
                String elemType = value.getClass().toString();
                throw new InvalidValueException("Unexpected element of type " + elemType + " in map of objects");
            }
        }
    }

    /**
     * Converts a collection of objects of the type srcBottomType to a collection of configurations.
     *
     * @param src           the collection of objects, may be nested, source
     * @param srcBottomType the type of objects
     * @param dst           the collection of configs, destination
     * @param parentConfig  the parent configuration, used to create the new configs to put in dst
     */
    private void convertObjectsToConfigs(
            Collection<?> src, Class<?> srcBottomType,
            Collection<Object> dst,
            Config parentConfig
    ) {
        for (Object element : src) {
            if (element == null) {
                dst.add(null);
            } else if (srcBottomType.isAssignableFrom(element.getClass())) {
                Config elementConfig = parentConfig.createSubConfig();
                convertToConfig(element, element.getClass(), elementConfig);

                dst.add(elementConfig);
            } else if (element instanceof Collection) {
                ArrayList<Object> subList = new ArrayList<>();
                convertObjectsToConfigs((Collection<?>)element, srcBottomType, subList, parentConfig);
                subList.trimToSize();

                dst.add(subList);
            } else {
                String elemType = element.getClass().toString();
                throw new InvalidValueException("Unexpected element of type " + elemType + " in collection of " + srcBottomType);
            }
        }
    }

    private <T> T createInstance(Class<T> tClass) {
        try {
            Constructor<T> constructor = tClass.getDeclaredConstructor();
            constructor.setAccessible(true);

            return constructor.newInstance();
        } catch (ReflectiveOperationException ex) {
            throw new ReflectionException("Unable to create an instance of " + tClass, ex);
        }
    }
}
