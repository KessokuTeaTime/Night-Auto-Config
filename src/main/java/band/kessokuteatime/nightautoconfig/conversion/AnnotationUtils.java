package band.kessokuteatime.nightautoconfig.conversion;

import band.kessokuteatime.nightautoconfig.annotation.GlobalConversion;
import band.kessokuteatime.nightautoconfig.annotation.GlobalConversions;
import band.kessokuteatime.nightautoconfig.annotation.TypeConversion;
import com.electronwill.nightconfig.core.conversion.*;
import com.electronwill.nightconfig.core.utils.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    }

    public static Optional<Converter<Object, Object>> getSingleConverter(Field field) {
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
        GlobalConversion globalConversion = null;
        if (object.getClass().isAnnotationPresent(GlobalConversions.class)) {
            GlobalConversions globalConversions = object.getClass().getAnnotation(GlobalConversions.class);
            globalConversion = Arrays.stream(globalConversions.value())
                   .filter(gc -> gc.target() == type)
                   .findFirst()
                   .orElse(null);
        } else if (object.getClass().isAnnotationPresent(GlobalConversion.class)) {
            GlobalConversion gc = object.getClass().getAnnotation(GlobalConversion.class);
            if (gc.target() == type) {
                globalConversion = gc;
            }
        }

        if (globalConversion != null) {
            try {
                var constructor = globalConversion.value().getDeclaredConstructor();
                constructor.setAccessible(true);

                return Optional.of((Converter<Object, Object>) constructor.newInstance());
            } catch (ReflectiveOperationException ex) {
                throw new ReflectionException("Cannot create a converter for type " + type, ex);
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
