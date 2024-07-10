package band.kessokuteatime.nightautoconfig.serde.deserializers;

import com.electronwill.nightconfig.core.serde.TypeConstraint;
import com.electronwill.nightconfig.core.serde.ValueDeserializer;
import com.electronwill.nightconfig.core.serde.ValueDeserializerProvider;

public interface UnifiedDeserializerProvider<T, R> extends ValueDeserializer<T, R>, ValueDeserializerProvider<T, R> {
    @Override
    default ValueDeserializer<T, R> provide(Class<?> valueClass, TypeConstraint resultType) {
        return this;
    }
}
