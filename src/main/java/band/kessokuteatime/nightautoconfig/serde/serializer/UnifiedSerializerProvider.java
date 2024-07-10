package band.kessokuteatime.nightautoconfig.serde.serializer;

import com.electronwill.nightconfig.core.serde.SerializerContext;
import com.electronwill.nightconfig.core.serde.ValueSerializer;
import com.electronwill.nightconfig.core.serde.ValueSerializerProvider;

public interface UnifiedSerializerProvider<T, R> extends ValueSerializer<T, R>, ValueSerializerProvider<T, R> {
    @Override
    default ValueSerializer<T, R> provide(Class<?> valueClass, SerializerContext ctx) {
        return this;
    }
}
