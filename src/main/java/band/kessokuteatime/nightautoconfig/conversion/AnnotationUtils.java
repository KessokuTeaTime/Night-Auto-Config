package band.kessokuteatime.nightautoconfig.conversion;

import band.kessokuteatime.nightautoconfig.NightAutoConfig;
import band.kessokuteatime.nightautoconfig.annotation.GlobalConversion;
import band.kessokuteatime.nightautoconfig.annotation.GlobalConversions;
import band.kessokuteatime.nightautoconfig.annotation.TypeConversion;
import com.electronwill.nightconfig.core.conversion.*;
import com.electronwill.nightconfig.core.utils.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

public final class AnnotationUtils {
    /**
     * Checks if an annotated element is annotated with {@link PreserveNotNull}.
     */
    public static boolean hasPreserveNotNull(AnnotatedElement annotatedElement) {
        return annotatedElement.isAnnotationPresent(PreserveNotNull.class);
    }

    /**
     * Checks if a field or its class is annotated with {@link PreserveNotNull}
     */
    public static boolean mustPreserve(Field field, Class<?> fieldClass) {
        return hasPreserveNotNull(field) || hasPreserveNotNull(fieldClass);
    }

    public static Optional<Converter<Object, Object>> getConverter(Object object, Field field) {
        // Global converters > type converters > field converters > default converters
        return getGlobalConverter(object, field.getType())
                .or(() -> getTypeConverter(field))
                .or(() -> getFieldConverter(field))
                .or(() -> getDefaultConverter(field.getType()));
    }

    public static Optional<Converter<Object, Object>> getDefaultConverter(Class<?> type) {
        return NightAutoConfig.DEFAULT_CONVERTERS.entrySet().stream()
                .filter(entry -> entry.getKey().test(type))
                .map(Map.Entry::getValue)
                .findFirst()
                .map(converterClass -> {
                    try {
                        return (Converter<Object, Object>) converterClass.newInstance();
                    } catch (ReflectiveOperationException ex) {
                        throw new ReflectionException("Cannot create a converter for type " + type, ex);
                    }
                });
    }

    public static Optional<Converter<Object, Object>> getFieldConverter(Field field) {
        if (field.isAnnotationPresent(Conversion.class)) {
            Conversion conversion = field.getAnnotation(Conversion.class);
            try {
                var constructor = conversion.value().getDeclaredConstructor();
                constructor.setAccessible(true);

                return Optional.of((Converter<Object, Object>) constructor.newInstance());
            } catch (ReflectiveOperationException ex) {
                throw new ReflectionException("Cannot create a converter for field " + field, ex);
            }
        }
        return Optional.empty();
    }

    public static Optional<Converter<Object, Object>> getTypeConverter(Field field) {
        if (field.isAnnotationPresent(TypeConversion.class)) {
            TypeConversion typeConversion = field.getAnnotation(TypeConversion.class);
            try {
                var constructor = typeConversion.value().getDeclaredConstructor();
                constructor.setAccessible(true);

                return Optional.of((Converter<Object, Object>) constructor.newInstance());
            } catch (ReflectiveOperationException ex) {
                throw new ReflectionException("Cannot create a converter for field " + field, ex);
            }
        }
        return Optional.empty();
    }

    public static Optional<Converter<Object, Object>> getGlobalConverter(Object object, Class<?> type) {
        List<GlobalConversion> globalConversionList = List.of();
        if (object.getClass().isAnnotationPresent(GlobalConversions.class)) {
            GlobalConversions globalConversions = object.getClass().getAnnotation(GlobalConversions.class);
            globalConversionList = List.of(globalConversions.value());
        } else if (object.getClass().isAnnotationPresent(GlobalConversion.class)) {
            globalConversionList = Collections.singletonList(object.getClass().getAnnotation(GlobalConversion.class));
        }

        if (!globalConversionList.isEmpty()) {
            for (GlobalConversion globalConversion : globalConversionList) {
                try {
                    var predicateConstructor = globalConversion.target().getDeclaredConstructor();
                    predicateConstructor.setAccessible(true);
                    var predicate = (Predicate<Class<?>>) predicateConstructor.newInstance();

                    if (predicate.test(type)) {
                        try {
                            var constructor = globalConversion.value().getDeclaredConstructor();
                            constructor.setAccessible(true);

                            return Optional.of((Converter<Object, Object>) constructor.newInstance());
                        } catch (ReflectiveOperationException ex) {
                            throw new ReflectionException("Cannot create a converter for type " + type, ex);
                        }
                    }
                } catch (ReflectiveOperationException ex) {
                    throw new ReflectionException("Cannot create a converter for type " + type, ex);
                }
            }
        }

        return Optional.empty();
    }

    public static List<String> getPath(Field field) {
        List<String> annotatedPath = getPath((AnnotatedElement)field);
        return (annotatedPath == null) ? Collections.singletonList(field.getName()) : annotatedPath;
    }

    public static List<String> getPath(AnnotatedElement annotatedElement) {
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
}
