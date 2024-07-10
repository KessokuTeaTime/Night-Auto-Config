package band.kessokuteatime.nightautoconfig.serde;

import band.kessokuteatime.nightautoconfig.serde.deserializer.FloatingPointDeserializer;
import com.electronwill.nightconfig.core.serde.*;

public class NightDeserializers {
    public static final FloatingPointDeserializer FLOATING_POINT = new FloatingPointDeserializer();

    public static void provideToBuilder(ObjectDeserializerBuilder builder) {
        builder.withDeserializerProvider((valueClass, resultType) -> resultType.getSatisfyingRawType().map(resultClass -> {
            if (FloatingPointDeserializer.isNumberTypeSupported(valueClass) && resultClass.isPrimitive()) {
                return FLOATING_POINT;
            }
            return (ValueDeserializer<Object, Object>) (value, resultType1, ctx) -> null;
        }).orElse(null));
    }
}
