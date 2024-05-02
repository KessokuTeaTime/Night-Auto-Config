package band.kessokuteatime.nightautoconfig.annotation;

import band.kessokuteatime.nightautoconfig.spec.ValuesInList;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a value to spec its acceptable values.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpecInList {
    Class<? extends ValuesInList<?>> definition();
}
