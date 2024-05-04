package band.kessokuteatime.nightautoconfig.annotation;

import band.kessokuteatime.nightautoconfig.conversion.api.StringSerializable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface KeySerializable {
    Class<? extends StringSerializable<?>> value();
}
