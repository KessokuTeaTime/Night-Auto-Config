package band.kessokuteatime.nightautoconfig.spec;

import band.kessokuteatime.nightautoconfig.annotation.*;
import com.electronwill.nightconfig.core.ConfigSpec;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public record SpecBuilder<T>(T t) {
    public ConfigSpec build() {
        ConfigSpec spec = new ConfigSpec();

        appendBasicSpecs(spec);
        appendInRangeSpecs(spec);

        return spec;
    }

    public void appendBasicSpecs(ConfigSpec spec) {
        Field[] fields = t.getClass().getDeclaredFields();

        Arrays.stream(fields)
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(t);

                        boolean isFloat = List.of(float.class, Float.class).contains(field.getType());

                        if (field.isAnnotationPresent(SpecElementValidator.class)) {
                            SpecElementValidator annotation = field.getAnnotation(SpecElementValidator.class);
                            Predicate<Object> validator = annotation.value().getDeclaredConstructor().newInstance();

                            if (isFloat) {
                                spec.define(field.getName(), ((Float) value).doubleValue(), validator);
                            } else {
                                spec.define(field.getName(), value, validator);
                            }
                        } else {
                            System.out.println(field.getName() + ", " + value);

                            if (isFloat) {
                                spec.define(field.getName(), ((Float) value).doubleValue());
                            } else {
                                spec.define(field.getName(), value);
                            }
                        }
                    } catch (IllegalAccessException | InvocationTargetException | InstantiationException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void appendInRangeSpecs(ConfigSpec spec) {
        Field[] fields = t.getClass().getDeclaredFields();

        // Int
        Arrays.stream(fields)
                .filter(field -> List.of(int.class, Integer.class).contains(field.getType()))
                .filter(field -> field.isAnnotationPresent(SpecInRangeInt.class))
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        SpecInRangeInt annotation = field.getAnnotation(SpecInRangeInt.class);
                        spec.defineInRange(field.getName(), (int) field.get(t), annotation.min(), annotation.max());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Long
        Arrays.stream(fields)
                .filter(field -> List.of(long.class, Long.class).contains(field.getType()))
                .filter(field -> field.isAnnotationPresent(SpecInRangeLong.class))
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        SpecInRangeLong annotation = field.getAnnotation(SpecInRangeLong.class);
                        spec.defineInRange(field.getName(), (long) field.get(t), annotation.min(), annotation.max());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Floating points
        Arrays.stream(fields)
                .filter(field -> List.of(double.class, Double.class, float.class, Float.class).contains(field.getType()))
                .filter(field -> field.isAnnotationPresent(SpecInRangeDouble.class))
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        SpecInRangeDouble annotation = field.getAnnotation(SpecInRangeDouble.class);
                        Object obj = field.get(t);

                        if (List.of(double.class, Double.class).contains(field.getType())) {
                            spec.defineInRange(field.getName(), (double) obj, annotation.min(), annotation.max());
                        } else if (List.of(float.class, Float.class).contains(field.getType())) {
                            spec.defineInRange(field.getName(), ((Float) obj).doubleValue(), annotation.min(), annotation.max());
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
