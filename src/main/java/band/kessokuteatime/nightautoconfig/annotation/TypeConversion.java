package band.kessokuteatime.nightautoconfig.annotation;

import com.google.common.base.Converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TypeConversion {
    Class<? extends Converter<?, ?>> value();
}
