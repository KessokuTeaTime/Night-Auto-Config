package band.kessokuteatime.nightautoconfig.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a value to spec its default value as the current definition.
 * This equals to:
 * <pre>
 *     spec.define("key", value);
 * </pre>
 *
 * If applied to a type, it marks the type to add {@link Spec} annotations to all of its fields. In result, all fields
 * of the type will be configured to have their default values.
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Spec {
}
