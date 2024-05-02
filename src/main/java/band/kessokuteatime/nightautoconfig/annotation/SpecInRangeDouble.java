package band.kessokuteatime.nightautoconfig.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpecInRangeDouble {
    double min() default 0;
    double max() default Double.MAX_VALUE;

    List<Class<?>> associatedTypes = List.of(Double.class, double.class, Float.class, float.class);
}
