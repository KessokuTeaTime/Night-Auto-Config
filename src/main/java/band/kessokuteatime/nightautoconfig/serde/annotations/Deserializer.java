package band.kessokuteatime.nightautoconfig.serde.annotations;

import com.electronwill.nightconfig.core.serde.ValueDeserializerProvider;

import java.lang.annotation.*;

@Repeatable(DeserializersContainer.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Deserializer {
    Class<? extends ValueDeserializerProvider<?, ?>> value();
}
