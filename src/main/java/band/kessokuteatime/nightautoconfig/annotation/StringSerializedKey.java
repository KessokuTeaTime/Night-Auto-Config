package band.kessokuteatime.nightautoconfig.annotation;

import band.kessokuteatime.nightautoconfig.converter.StringIdentityConverter;
import com.electronwill.nightconfig.core.conversion.Converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StringSerializedKey {
    Class<? extends Converter<?, String>> definition() default StringIdentityConverter.class;
}
