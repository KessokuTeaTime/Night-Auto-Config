package band.kessokuteatime.nightautoconfig.serde.annotations;

import com.electronwill.nightconfig.core.serde.ValueDeserializerProvider;

import java.lang.annotation.*;

@Repeatable(DeserializerProvidersContainer.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DeserializerProvider {
    Class<? extends ValueDeserializerProvider<?, ?>> value();
}
