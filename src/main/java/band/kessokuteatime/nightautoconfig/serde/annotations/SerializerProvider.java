package band.kessokuteatime.nightautoconfig.serde.annotations;

import com.electronwill.nightconfig.core.serde.ValueSerializerProvider;

import java.lang.annotation.*;

@Repeatable(SerializerProvidersContainer.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SerializerProvider {
    Class<? extends ValueSerializerProvider<?, ?>> value();
}
