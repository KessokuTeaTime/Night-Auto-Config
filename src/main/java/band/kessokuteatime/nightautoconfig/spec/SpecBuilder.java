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

                        if (field.isAnnotationPresent(SpecElementValidator.class)) {
                            SpecElementValidator annotation = field.getAnnotation(SpecElementValidator.class);
                            Predicate<Object> validator = annotation.value().getDeclaredConstructor().newInstance();
                            spec.define(field.getName(), field.get(t), validator);
                        } else {
                            System.out.println(field.get(t));
                            spec.define(field.getName(), field.get(t));
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
                        double value;

                        if (obj instanceof Double) {
                            value = (Double) obj;
                        } else {
                            value = ((Float) obj).doubleValue();
                        }

                        spec.defineInRange(field.getName(), value, annotation.min(), annotation.max());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
