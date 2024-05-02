package band.kessokuteatime.nightautoconfig.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpecInRangeByte {
    byte min() default 0;
    byte max() default Byte.MAX_VALUE;
    
    List<Class<?>> associatedTypes = List.of(Byte.class, byte.class);
}
