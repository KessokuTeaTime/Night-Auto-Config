package band.kessokuteatime.nightautoconfig.spec;

import band.kessokuteatime.nightautoconfig.annotation.*;
import band.kessokuteatime.nightautoconfig.serializer.base.ConfigType;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.conversion.*;
import com.electronwill.nightconfig.core.utils.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static band.kessokuteatime.nightautoconfig.NightAutoConfig.LOGGER;

public record Specs(Object object, ConfigType type, List<String> nestedPaths) {
    public enum Session {
        SAVING, LOADING;
    }

    public Specs(Object object, ConfigType type, String fileName) {
        this(object, type, Collections.singletonList(fileName));
    }

    public int correct(Config config, Session session) {
        /*
        // Process nested configurations
        AtomicInteger nestedCorrections = new AtomicInteger();
        Arrays.stream(nestedFields())
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(object);

                        List<String> paths = getPath(field);
                        List<String> deeperNestedPaths = Stream.concat(nestedPaths.stream(), paths.stream()).toList();
                        Config nestedConfig = config.get(paths);

                        Specs nestedSpecs = new Specs(value, type, deeperNestedPaths);
                        if (nestedConfig != null) {
                            // Correct the nested config
                            nestedCorrections.addAndGet(nestedSpecs.correct(nestedConfig, session));
                        } else {
                            // Correct and fallback to the default config (shouldn'object happen)
                            Config fallbackConfig = type.wrap(value);
                            nestedSpecs.correct(fallbackConfig, session);
                            config.set(paths, convertValue(field, fallbackConfig));

                            nestedCorrections.addAndGet(fallbackConfig.entrySet().size());
                            LOGGER.warn(
                                    "{} Missing nested configuration for {}! Falling back to the default one",
                                    loggerPrefix(session), String.join(".", paths)
                            );
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        // Process missing enums
        AtomicInteger enumCorrections = new AtomicInteger();
        Arrays.stream(nonNestedFields())
                .filter(field -> field.getType().isEnum())
                .forEach(field -> {
                    List<String> paths = getPath(field);

                    if (!config.contains(paths)) {
                        try {
                            field.setAccessible(true);
                            Object value = field.get(object);

                            if (value != null) {
                                config.set(paths, convertValue(field, value));

                                enumCorrections.incrementAndGet();
                                LOGGER.warn(
                                        "{} Missing enum for {}! Falling back to the default one",
                                        loggerPrefix(session), String.join(".", paths)
                                );
                            } else {
                                LOGGER.error(
                                        "{} Missing enum for {} while the default value is null!",
                                        loggerPrefix(session), String.join(".", paths)
                                );
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

        ConfigSpec spec = new ConfigSpec();

        appendNestedSpecs(spec);
        appendBasicSpecs(spec);
        appendInRangeSpecs(spec);
        appendInListSpecs(spec);
        appendOfClassSpecs(spec);
        appendEnumSpecs(spec);

        ConfigSpec.CorrectionListener listener = (action, path, incorrectValue, correctedValue) -> {
            String pathString = String.join(",", path);
            LOGGER.info(
                    "{} Corrected {}: was {}, is now {}",
                    loggerPrefix(session), pathString, incorrectValue, correctedValue
            );
        };

        final int corrections = spec.correct(config, listener) + enumCorrections.get();
        final int totalCorrections = corrections + nestedCorrections.get();

        if (nestedCorrections.get() == 0) {
            if (corrections > 0) {
                LOGGER.info(
                        "{} Corrected {} {}",
                        loggerPrefix(session), corrections, (corrections == 1)? "item" : "items"
                );
            }
        } else {
            LOGGER.info(
                    "{} Corrected {} {} in total ({} nested)",
                    loggerPrefix(session), totalCorrections, (totalCorrections == 1)? "item" : "items", nestedCorrections
            );
        }

        return totalCorrections;

         */
        ConfigSpec spec = new ConfigSpec();
        Arrays.stream(fields()).forEach(field -> {
            try {
                field.setAccessible(true);
                Object value = field.get(object);

                List<String> paths = getPath(field);
                spec.define(paths, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return spec.correct(config);
    }

    /**
     * Gets the path of a field: returns the annotated path, or the field's name if there is no
     * annotated path.
     *
     * @return the annotated path, if any, or the field name
     */
    static List<String> getPath(Field field) {
        List<String> annotatedPath = getPath((AnnotatedElement)field);
        return (annotatedPath == null) ? Collections.singletonList(field.getName()) : annotatedPath;
    }
    /**
     * Gets the annotated path (specified with @Path or @AdvancedPath) of an annotated element.
     *
     * @return the annotated path, or {@code null} if there is none.
     */
    static List<String> getPath(AnnotatedElement annotatedElement) {
        Path path = annotatedElement.getDeclaredAnnotation(Path.class);
        if (path != null) {
            return StringUtils.split(path.value(), '.');
        }

        AdvancedPath advancedPath = annotatedElement.getDeclaredAnnotation(AdvancedPath.class);
        if (advancedPath != null) {
            return Arrays.asList(advancedPath.value());
        }

        return null;
    }

    static Double safeDouble(Object floatObject) {
        return ((Float) floatObject).doubleValue();
    }

    static Predicate<? super Field> typeChecker(List<Class<?>> availableTypes) {
        return field -> availableTypes.contains(field.getType());
    }

    static boolean isNested(Field field) {
        return field.isAnnotationPresent(Nested.class) || field.getType().isAnnotationPresent(Nested.class);
    }

    private boolean isComplexType(Class<?> type) {
        return !type.isEnum() && (
                !this.type.format().supportsType(type)
                        || type.isAnnotationPresent(Nested.class)
                        || type.isArray()
                        || List.class.isAssignableFrom(type)
                        || Map.class.isAssignableFrom(type)
        );
    }

    private Object getValue(Field field) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        field.setAccessible(true);
        Object value = field.get(object);

        return convertValue(field, value);
    }

    private Object convertValue(Field field, Object value) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        /*
        Object converted;

        if (field.isAnnotationPresent(Conversion.class)) {
            Conversion conversion = field.getAnnotation(Conversion.class);
            Converter<Object, Object> converter = (Converter<Object, Object>) conversion.value().getDeclaredConstructor().newInstance();

            converted = converter.convertFromField(value);
        } else if (isComplexType(value.getClass())) {
            System.out.println(value);
            converted = ConfigType.DEFAULT_SIMPLE.wrap(value);
            System.out.println(converted);
        } else {
            converted = value;
        }

        // Convert to universal config if value is a configuration instance
        if (converted instanceof Config) {
            converted = ConfigType.DEFAULT_SIMPLE.wrap(converted);
        }

        return converted;

         */
        return value;
    }

    private String loggerPrefix(Session session) {
        return String.format("(%s)[%s]", session, String.join(" -> ", nestedPaths));
    }

    private Field[] fields() {
        return object.getClass().getDeclaredFields();
    }

    private Field[] nestedFields() {
        return Arrays.stream(fields())
                    .filter(Specs::isNested)
                    .toArray(Field[]::new);
    }

    private Field[] nonNestedFields() {
        return Arrays.stream(fields())
                    .filter(field -> !isNested(field))
                    .toArray(Field[]::new);
    }

    private void appendNestedSpecs(ConfigSpec spec) {
        Arrays.stream(nestedFields())
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(object);

                        Config nestedConfig = type.wrap(value);
                        spec.define(getPath(field), convertValue(field, nestedConfig));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void appendBasicSpecs(ConfigSpec spec) {
        Arrays.stream(nonNestedFields())
                .filter(field -> !field.getType().isEnum()) // Enums are handled separately
                .forEach(field -> {
                    try {
                        if (isNested(field)) {
                            return;
                        }

                        if (field.isAnnotationPresent(SpecValidator.class)) {
                            SpecValidator validatorAnnotation = field.getAnnotation(SpecValidator.class);
                            Predicate<Object> validator = validatorAnnotation.value().getDeclaredConstructor().newInstance();

                            spec.define(getPath(field), getValue(field), validator);
                        } else {
                            spec.define(getPath(field), getValue(field));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private <V extends Comparable<? super V>, E extends Comparable<E>> void appendInRangeSpec(
            ConfigSpec spec, Field field,
            V min, V max
    ) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        field.setAccessible(true);
        appendInRangeSpec(spec, field, (E) getValue(field), (E) convertValue(field, min), (E) convertValue(field, max));
    }

    private <V extends Comparable<? super V>> void appendInRangeSpec(
            ConfigSpec spec, Field field,
            V value, V min, V max
    ) {
        spec.defineInRange(getPath(field), value, min, max);
    }

    private <V extends Comparable<? super V>, E extends Comparable<E>> void appendInRangeSpec(ConfigSpec spec, Field field) {
        SpecInRange inRangeAnnotation = field.getAnnotation(SpecInRange.class);
        try {
            InRangeProvider<?> inRangeProvider = inRangeAnnotation.definition().getDeclaredConstructor().newInstance();
            if (field.getType() == inRangeProvider.min().getClass()) {
                appendInRangeSpec(spec, field, (E) getValue(field), (E) convertValue(field, inRangeProvider.min()), (E) convertValue(field, inRangeProvider.max()));
            } else {
                LOGGER.error(
                        "Invalid @{} annotation for {}: range values must be of the same type as the field. Ignoring!",
                        SpecInRange.class.getSimpleName(), getPath(field)
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void appendInRangeSpecs(ConfigSpec spec) {
        Field[] fields = nonNestedFields();

        // General
        Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(SpecInRange.class))
                .forEach(field -> appendInRangeSpec(spec, field));

        // Double
        Arrays.stream(fields)
                .filter(typeChecker(List.of(Double.class, double.class)))
                .filter(field -> field.isAnnotationPresent(SpecDoubleInRange.class))
                .forEach(field -> {
                    SpecDoubleInRange inRangeAnnotation = field.getAnnotation(SpecDoubleInRange.class);
                    try {
                        appendInRangeSpec(spec, field, inRangeAnnotation.min(), inRangeAnnotation.max());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        // Float
        Arrays.stream(fields)
                .filter(typeChecker(List.of(Float.class, float.class)))
                .filter(field -> field.isAnnotationPresent(SpecFloatInRange.class))
                .forEach(field -> {
                    SpecFloatInRange inRangeAnnotation = field.getAnnotation(SpecFloatInRange.class);
                    try {
                        appendInRangeSpec(spec, field, inRangeAnnotation.min(), inRangeAnnotation.max());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        // Long
        Arrays.stream(fields)
                .filter(typeChecker(List.of(Long.class, long.class)))
                .filter(field -> field.isAnnotationPresent(SpecLongInRange.class))
                .forEach(field -> {
                    SpecLongInRange inRangeAnnotation = field.getAnnotation(SpecLongInRange.class);
                    try {
                        appendInRangeSpec(spec, field, inRangeAnnotation.min(), inRangeAnnotation.max());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        // Int
        Arrays.stream(fields)
                .filter(typeChecker(List.of(Integer.class, int.class)))
                .filter(field -> field.isAnnotationPresent(SpecIntInRange.class))
                .forEach(field -> {
                    SpecIntInRange inRangeAnnotation = field.getAnnotation(SpecIntInRange.class);
                    try {
                        appendInRangeSpec(spec, field, inRangeAnnotation.min(), inRangeAnnotation.max());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        // String
        Arrays.stream(fields)
                .filter(typeChecker(List.of(String.class)))
                .filter(field -> field.isAnnotationPresent(SpecStringInRange.class))
                .forEach(field -> {
                    SpecStringInRange inRangeAnnotation = field.getAnnotation(SpecStringInRange.class);
                    try {
                        appendInRangeSpec(spec, field, inRangeAnnotation.min(), inRangeAnnotation.max());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void appendInListSpecs(ConfigSpec spec) {
        Arrays.stream(nonNestedFields())
                .filter(field -> field.isAnnotationPresent(SpecInList.class))
                .filter(field -> !field.getType().isEnum()) // Enums are handled separately
                .forEach(field -> {
                    SpecInList inListAnnotation = field.getAnnotation(SpecInList.class);
                    try {
                        InListProvider<?> inListProvider = inListAnnotation.definition().getDeclaredConstructor().newInstance();
                        Collection<?> acceptableValues = inListProvider.acceptableValues();

                        spec.defineInList(getPath(field), getValue(field), acceptableValues);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private <E> void appendOfClassSpec(ConfigSpec spec, Field field) {
        SpecOfClass ofClassAnnotation = field.getAnnotation(SpecOfClass.class);
        try {
            Class<?> ofClass = ofClassAnnotation.value();

            if (ofClass.isAssignableFrom(field.getType())) {
                spec.defineOfClass(getPath(field), (E) getValue(field), (Class<? super E>) ofClass);
            } else {
                LOGGER.error(
                        "Invalid @{} annotation for {}: the field type is not a subclass of the specified class (the specified class is not assignable from the field type). Ignoring!",
                        SpecOfClass.class.getSimpleName(), getPath(field)
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void appendOfClassSpecs(ConfigSpec spec) {
        Arrays.stream(nonNestedFields())
                .filter(field -> !field.getType().isEnum()) // Enums are handled separately
                .filter(field -> field.isAnnotationPresent(SpecOfClass.class))
                .forEach(field -> appendOfClassSpec(spec, field));
    }

    private <E extends Enum<E>> void appendEnumSpec(ConfigSpec spec, Field field) {
        SpecEnum specEnum = field.getAnnotation(SpecEnum.class);
        EnumGetMethod enumGetMethod = (specEnum != null) ? specEnum.method() : EnumGetMethod.NAME_IGNORECASE;
        try {
            if (field.isAnnotationPresent(SpecInList.class)) {
                // Restricted
                SpecInList inListAnnotation = field.getAnnotation(SpecInList.class);
                Collection<?> acceptableValues = inListAnnotation.definition().getDeclaredConstructor().newInstance().acceptableValues();

                if (acceptableValues.stream().allMatch(v -> v.getClass().isEnum() && v.getClass() == field.getType())) {
                    Collection<E> acceptableEnumValues = (Collection<E>) acceptableValues;
                    spec.defineRestrictedEnum(getPath(field), (E) getValue(field), acceptableEnumValues, enumGetMethod);
                } else {
                    LOGGER.error(
                            "Invalid @{} annotation for {}: acceptable values must be enums of the same type as the field. Ignoring!",
                            SpecInList.class.getSimpleName(), getPath(field)
                    );
                }
            } else if (field.isAnnotationPresent(SpecOfClass.class)) {
                // Restricted by class
                // Currently cannot be handled by `defineOfClass`
                SpecOfClass ofClassAnnotation = field.getAnnotation(SpecOfClass.class);
                Class<?> ofClass = ofClassAnnotation.value();
                if (ofClass.isAssignableFrom(field.getType())) {
                    spec.defineRestrictedEnum(getPath(field), (E) getValue(field), (List<E>) List.of(ofClass.getEnumConstants()), enumGetMethod);
                } else {
                    LOGGER.error(
                            "Invalid @{} annotation for {}: the field type is not a subclass of the specified class (the specified class is not assignable from the field type). Ignoring!",
                            SpecOfClass.class.getSimpleName(), getPath(field)
                    );
                }
            }else {
                // Unrestricted
                spec.defineEnum(getPath(field), (E) getValue(field), enumGetMethod);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void appendEnumSpecs(ConfigSpec spec) {
        Arrays.stream(nonNestedFields())
                .filter(field -> field.getType().isEnum())
                .forEach(field -> appendEnumSpec(spec, field));
    }
}
