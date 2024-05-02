package band.kessokuteatime.nightautoconfig.annotation;

import com.electronwill.nightconfig.core.EnumGetMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a value to spec its {@link EnumGetMethod}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpecEnumGetMethod {
    EnumGetMethod value();
}
