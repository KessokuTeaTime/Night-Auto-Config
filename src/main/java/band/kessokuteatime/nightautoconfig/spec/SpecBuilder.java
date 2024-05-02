package band.kessokuteatime.nightautoconfig.spec;

import band.kessokuteatime.nightautoconfig.annotation.*;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.AdvancedPath;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public record SpecBuilder<T>(T t) {
    public ConfigSpec build() {
        ConfigSpec spec = new ConfigSpec();

        appendBasicSpecs(spec);
        appendInRangeSpecs(spec);

        return spec;
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

    private Field[] fields() {
        return t.getClass().getDeclaredFields();
    }

    private void appendBasicSpecs(ConfigSpec spec) {
        Arrays.stream(fields())
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(t);

                        boolean isFloat = List.of(float.class, Float.class).contains(field.getType());

                        if (field.isAnnotationPresent(SpecElementValidator.class)) {
                            SpecElementValidator annotation = field.getAnnotation(SpecElementValidator.class);
                            Predicate<Object> validator = annotation.value().getDeclaredConstructor().newInstance();

                            if (isFloat) {
                                // Store float as double
                                spec.define(getPath(field), safeDouble(value), validator);
                            } else {
                                spec.define(getPath(field), value, validator);
                            }
                        } else {
                            if (isFloat) {
                                // Store float as double
                                spec.define(getPath(field), safeDouble(value));
                            } else {
                                spec.define(getPath(field), value);
                            }
                        }
                    } catch (IllegalAccessException | InvocationTargetException | InstantiationException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private <V extends Comparable<? super V>> void appendInRangeSpec(
            ConfigSpec spec, Field field,
            V min, V max
    ) throws IllegalAccessException {
        field.setAccessible(true);
        appendInRangeSpec(spec, field, (V) field.get(t), min, max);
    }

    private <V extends Comparable<? super V>> void appendInRangeSpec(
            ConfigSpec spec, Field field,
            V value, V min, V max
    ) {
        spec.defineInRange(getPath(field), value, min, max);
    }

    private void appendInRangeSpecs(ConfigSpec spec) {
        Field[] fields = t.getClass().getDeclaredFields();

        // Byte
        Arrays.stream(fields)
                .filter(typeChecker(SpecInRangeByte.associatedTypes))
                .filter(field -> field.isAnnotationPresent(SpecInRangeByte.class))
                .forEach(field -> {
                    SpecInRangeByte annotation = field.getAnnotation(SpecInRangeByte.class);
                    try {
                        appendInRangeSpec(spec, field, annotation.min(), annotation.max());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Short
        Arrays.stream(fields)
                .filter(typeChecker(SpecInRangeShort.associatedTypes))
                .filter(field -> field.isAnnotationPresent(SpecInRangeShort.class))
                .forEach(field -> {
                    SpecInRangeShort annotation = field.getAnnotation(SpecInRangeShort.class);
                    try {
                        appendInRangeSpec(spec, field, annotation.min(), annotation.max());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Int
        Arrays.stream(fields)
                .filter(typeChecker(SpecInRangeInt.associatedTypes))
                .filter(field -> field.isAnnotationPresent(SpecInRangeInt.class))
                .forEach(field -> {
                    SpecInRangeInt annotation = field.getAnnotation(SpecInRangeInt.class);
                    try {
                        appendInRangeSpec(spec, field, annotation.min(), annotation.max());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Long
        Arrays.stream(fields)
                .filter(typeChecker(SpecInRangeLong.associatedTypes))
                .filter(field -> field.isAnnotationPresent(SpecInRangeLong.class))
                .forEach(field -> {
                    SpecInRangeLong annotation = field.getAnnotation(SpecInRangeLong.class);
                    try {
                        appendInRangeSpec(spec, field, annotation.min(), annotation.max());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Float
        Arrays.stream(fields)
                .filter(typeChecker(List.of(Float.class, float.class)))
                .filter(field -> field.isAnnotationPresent(SpecInRangeDouble.class))
                .forEach(field -> {
                    SpecInRangeDouble annotation = field.getAnnotation(SpecInRangeDouble.class);
                    try {
                        appendInRangeSpec(spec, field, safeDouble(field.get(t)), annotation.min(), annotation.max());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Double
        Arrays.stream(fields)
                .filter(typeChecker(List.of(Double.class, double.class)))
                .filter(field -> field.isAnnotationPresent(SpecInRangeDouble.class))
                .forEach(field -> {
                    SpecInRangeDouble annotation = field.getAnnotation(SpecInRangeDouble.class);
                    try {
                        appendInRangeSpec(spec, field, (double) field.get(t), annotation.min(), annotation.max());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
