package band.kessokuteatime.nightautoconfig.serde.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DeserializerProvidersContainer {
    DeserializerProvider[] value();
}
