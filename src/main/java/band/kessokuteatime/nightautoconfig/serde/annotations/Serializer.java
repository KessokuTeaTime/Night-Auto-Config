package band.kessokuteatime.nightautoconfig.serde.annotations;

import com.electronwill.nightconfig.core.serde.ValueSerializerProvider;

import java.lang.annotation.*;

@Repeatable(SerializersContainer.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Serializer {
    Class<? extends ValueSerializerProvider<?, ?>> value();
}
