package band.kessokuteatime.nightautoconfig.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpecInRangeShort {
    short min() default 0;
    short max() default Short.MAX_VALUE;

    List<Class<?>> associatedTypes = List.of(Short.class, short.class);
}
