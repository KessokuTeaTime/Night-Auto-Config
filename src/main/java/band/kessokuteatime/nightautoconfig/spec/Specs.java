package band.kessokuteatime.nightautoconfig.spec;

import band.kessokuteatime.nightautoconfig.NightAutoConfig;
import band.kessokuteatime.nightautoconfig.annotation.*;
import band.kessokuteatime.nightautoconfig.serializer.ConfigType;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.AdvancedPath;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.utils.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public record Specs<T>(T t, ConfigType type, String fileName) {
    public enum Session {
        SAVING("Saving"),
        NESTED_SAVING("Saving/Nested"),
        LOADING("Loading"),
        NESTED_LOADING("Loading/Nested"),
        UNKNOWN("Unknown");

        private final String name;

        Session(String name) {
            this.name = name;
        }

        public String semanticName() {
            return name;
        }

        public Session nested() {
            return switch (this) {
                case SAVING, NESTED_SAVING -> NESTED_SAVING;
                case LOADING, NESTED_LOADING -> NESTED_LOADING;
                default -> UNKNOWN;
            };
        }
    }

    public int correct(Config config, Session session) {
        ConfigSpec spec = new ConfigSpec();

        appendNestedSpecs(spec);
        appendBasicSpecs(spec);
        appendInRangeSpecs(spec);

        ConfigSpec.CorrectionListener listener = (action, path, incorrectValue, correctedValue) -> {
            String pathString = String.join(",", path);
            NightAutoConfig.LOGGER.info(
                    "{} Corrected {}: was {}, is now {}",
                    loggerPrefix(session), pathString, incorrectValue, correctedValue
            );
        };

        // Process nested configurations
        AtomicInteger nestedCorrections = new AtomicInteger();
        Arrays.stream(fields())
                .filter(Specs::isNested)
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(t);

                        List<String> path = getPath(field);
                        Config nestedConfig = config.get(path);

                        Specs<Object> nestedSpecs = new Specs<>(value, type, fileName);
                        if (nestedConfig != null) {
                            // Correct the nested config
                            nestedCorrections.addAndGet(nestedSpecs.correct(nestedConfig, session.nested()));
                        } else {
                            // Correct and fallback to the default config (shouldn't happen)
                            Config fallbackConfig = type.wrap(value);
                            nestedCorrections.addAndGet(nestedSpecs.correct(fallbackConfig, session.nested()));
                            config.set(path, fallbackConfig);

                            NightAutoConfig.LOGGER.warn(
                                    "{} Missing nested config for {}! Falling back to the default one.",
                                    loggerPrefix(session), String.join(".", path)
                            );
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        final int corrections = spec.correct(config, listener);
        final int totalCorrections = corrections + nestedCorrections.get();

        if (nestedCorrections.get() == 0) {
            if (corrections > 0) {
                NightAutoConfig.LOGGER.info(
                        "{} Corrected {} {}",
                        loggerPrefix(session), corrections, (corrections == 1)? "item" : "items"
                );
            }
        } else {
            NightAutoConfig.LOGGER.info(
                    "{} Corrected {} {} in total ({} nested)",
                    loggerPrefix(session), totalCorrections, (totalCorrections == 1)? "item" : "items", nestedCorrections
            );
        }

        return totalCorrections;
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
        return field.getType().isAnnotationPresent(Nested.class);
    }

    private String loggerPrefix(Session session) {
        return String.format("[%s](%s)", fileName, session.semanticName());
    }

    private Field[] fields() {
        return t.getClass().getDeclaredFields();
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
                        Object value = field.get(t);

                        Config nestedConfig = type.wrap(value);
                        spec.define(getPath(field), nestedConfig);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void appendBasicSpecs(ConfigSpec spec) {
        Arrays.stream(nonNestedFields())
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(t);

                        if (isNested(field)) {
                            return;
                        }

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
        Field[] fields = nonNestedFields();

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
