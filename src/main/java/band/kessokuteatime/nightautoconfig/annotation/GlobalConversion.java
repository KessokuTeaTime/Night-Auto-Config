package band.kessokuteatime.nightautoconfig.annotation;

import com.electronwill.nightconfig.core.conversion.Converter;

import java.lang.annotation.*;
import java.util.function.Predicate;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(GlobalConversions.class)
public @interface GlobalConversion {
    Class<Predicate<Class<?>>> target();
    Class<? extends Converter<?, ?>> value();
}
