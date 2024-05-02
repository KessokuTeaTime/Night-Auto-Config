package band.kessokuteatime.nightautoconfig.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a type to become a nestable configuration class inside a {@link me.shedaniel.autoconfig.ConfigData} type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Nested {
}
