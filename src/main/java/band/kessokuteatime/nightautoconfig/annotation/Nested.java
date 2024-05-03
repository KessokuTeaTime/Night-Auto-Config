package band.kessokuteatime.nightautoconfig.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a type or a field to become nestable as sub-configs inside a {@link me.shedaniel.autoconfig.ConfigData} instance.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Nested {
}
