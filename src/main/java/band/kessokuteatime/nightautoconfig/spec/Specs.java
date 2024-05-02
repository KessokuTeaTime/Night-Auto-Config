package band.kessokuteatime.nightautoconfig.spec;

import band.kessokuteatime.nightautoconfig.NightAutoConfig;
import band.kessokuteatime.nightautoconfig.annotation.*;
import band.kessokuteatime.nightautoconfig.serializer.ConfigType;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.conversion.AdvancedPath;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.SpecEnum;
import com.electronwill.nightconfig.core.utils.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static band.kessokuteatime.nightautoconfig.NightAutoConfig.LOGGER;

public record Specs<T>(T t, ConfigType type, List<String> nestedPaths) {
    public enum Session {
        SAVING, LOADING;
    }

    public Specs(T t, ConfigType type, String fileName) {
        this(t, type, Collections.singletonList(fileName));
    }

    public int correct(Config config, Session session) {
        // Process nested configurations
        AtomicInteger nestedCorrections = new AtomicInteger();
        Arrays.stream(nestedFields())
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(t);

                        List<String> paths = getPath(field);
                        List<String> deeperNestedPaths = Stream.concat(nestedPaths.stream(), paths.stream()).toList();
                        Config nestedConfig = config.get(paths);

                        Specs<Object> nestedSpecs = new Specs<>(value, type, deeperNestedPaths);
                        if (nestedConfig != null) {
                            // Correct the nested config
                            nestedCorrections.addAndGet(nestedSpecs.correct(nestedConfig, session));
                        } else {
                            // Correct and fallback to the default config (shouldn't happen)
                            Config fallbackConfig = type.wrap(value);
                            nestedSpecs.correct(fallbackConfig, session);
                            config.set(paths, fallbackConfig);

                            nestedCorrections.addAndGet(fallbackConfig.entrySet().size());
                            LOGGER.warn(
                                    "{} Missing nested configuration for {}! Falling back to the default one",
                                    loggerPrefix(session), String.join(".", paths)
                            );
                        }
                    } catch (IllegalAccessException e) {
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
                            Object value = field.get(t);

                            if (value != null) {
                                config.set(paths, value);

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
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

        ConfigSpec spec = new ConfigSpec();

        appendNestedSpecs(spec);
        appendBasicSpecs(spec);
        appendInRangeSpecs(spec);
        appendInListSpecs(spec);

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
        return String.format("(%s)[%s]", session, String.join(" -> ", nestedPaths));
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

    private <E extends Enum<E>> void appendBasicSpecs(ConfigSpec spec) {
        Arrays.stream(nonNestedFields())
                .forEach(field -> {
                    try {
                        if (isNested(field)) {
                            return;
                        }

                        field.setAccessible(true);
                        Object value = field.get(t);

                        final boolean isEnum = field.getType().isEnum();
                        final boolean isFloat = List.of(float.class, Float.class).contains(field.getType());

                        if (isEnum) {
                            SpecEnum specEnum = field.getAnnotation(SpecEnum.class);
                            EnumGetMethod enumGetMethod = (specEnum != null) ? specEnum.method() : EnumGetMethod.NAME_IGNORECASE;

                            if (field.isAnnotationPresent(SpecInList.class)) {
                                // Restricted
                                SpecInList inListAnnotation = field.getAnnotation(SpecInList.class);
                                Collection<?> acceptableValues = inListAnnotation.definition().getDeclaredConstructor().newInstance().acceptableValues();

                                if (acceptableValues.stream().allMatch(v -> v.getClass().isEnum() && v.getClass() == field.getType())) {
                                    Collection<E> acceptableEnumValues = (Collection<E>) acceptableValues;
                                    spec.defineRestrictedEnum(getPath(field), (E) value, acceptableEnumValues, enumGetMethod);
                                } else {
                                    LOGGER.error(
                                            "Invalid @SpecInList annotation for {}: acceptable values must be enums of the same type as the field. Ignoring!",
                                            getPath(field)
                                    );
                                }
                            } else {
                                // Unrestricted
                                spec.defineEnum(getPath(field), (E) value, enumGetMethod);
                            }
                        } else if (field.isAnnotationPresent(SpecElementValidator.class)) {
                            SpecElementValidator elementValidatorAnnotation = field.getAnnotation(SpecElementValidator.class);
                            Predicate<Object> validator = elementValidatorAnnotation.definition().getDeclaredConstructor().newInstance();

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
                    } catch (Exception e) {
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
                    SpecInRangeByte inRangeAnnotation = field.getAnnotation(SpecInRangeByte.class);
                    try {
                        appendInRangeSpec(spec, field, inRangeAnnotation.min(), inRangeAnnotation.max());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Short
        Arrays.stream(fields)
                .filter(typeChecker(SpecInRangeShort.associatedTypes))
                .filter(field -> field.isAnnotationPresent(SpecInRangeShort.class))
                .forEach(field -> {
                    SpecInRangeShort inRangeAnnotation = field.getAnnotation(SpecInRangeShort.class);
                    try {
                        appendInRangeSpec(spec, field, inRangeAnnotation.min(), inRangeAnnotation.max());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Int
        Arrays.stream(fields)
                .filter(typeChecker(SpecInRangeInt.associatedTypes))
                .filter(field -> field.isAnnotationPresent(SpecInRangeInt.class))
                .forEach(field -> {
                    SpecInRangeInt inRangeAnnotation = field.getAnnotation(SpecInRangeInt.class);
                    try {
                        appendInRangeSpec(spec, field, inRangeAnnotation.min(), inRangeAnnotation.max());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Long
        Arrays.stream(fields)
                .filter(typeChecker(SpecInRangeLong.associatedTypes))
                .filter(field -> field.isAnnotationPresent(SpecInRangeLong.class))
                .forEach(field -> {
                    SpecInRangeLong inRangeAnnotation = field.getAnnotation(SpecInRangeLong.class);
                    try {
                        appendInRangeSpec(spec, field, inRangeAnnotation.min(), inRangeAnnotation.max());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Float
        Arrays.stream(fields)
                .filter(typeChecker(List.of(Float.class, float.class)))
                .filter(field -> field.isAnnotationPresent(SpecInRangeDouble.class))
                .forEach(field -> {
                    SpecInRangeDouble inRangeAnnotation = field.getAnnotation(SpecInRangeDouble.class);
                    try {
                        appendInRangeSpec(spec, field, safeDouble(field.get(t)), inRangeAnnotation.min(), inRangeAnnotation.max());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Double
        Arrays.stream(fields)
                .filter(typeChecker(List.of(Double.class, double.class)))
                .filter(field -> field.isAnnotationPresent(SpecInRangeDouble.class))
                .forEach(field -> {
                    SpecInRangeDouble inRangeAnnotation = field.getAnnotation(SpecInRangeDouble.class);
                    try {
                        appendInRangeSpec(spec, field, (double) field.get(t), inRangeAnnotation.min(), inRangeAnnotation.max());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void appendInListSpecs(ConfigSpec spec) {
        Arrays.stream(nonNestedFields())
                .filter(field -> field.isAnnotationPresent(SpecInList.class))
                .filter(field -> !field.getType().isEnum())
                .forEach(field -> {
                    SpecInList inListAnnotation = field.getAnnotation(SpecInList.class);
                    try {
                        field.setAccessible(true);
                        Object value = field.get(t);

                        Collection<?> acceptableValues = inListAnnotation.definition().getDeclaredConstructor().newInstance().acceptableValues();

                        spec.defineInList(getPath(field), value, acceptableValues);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
