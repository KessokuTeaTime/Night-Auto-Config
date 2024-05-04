package band.kessokuteatime.nightautoconfig.annotation;

import com.electronwill.nightconfig.core.conversion.Converter;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(GlobalConversions.class)
public @interface GlobalConversion {
    Class<?> target();
    Class<? extends Converter<?, ?>> value();
}
