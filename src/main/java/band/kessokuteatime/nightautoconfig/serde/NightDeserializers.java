package band.kessokuteatime.nightautoconfig.serde;

import band.kessokuteatime.nightautoconfig.serde.deserializers.ColorDeserializer;
import band.kessokuteatime.nightautoconfig.serde.deserializers.FloatingPointDeserializer;
import band.kessokuteatime.nightautoconfig.util.TypeUtil;
import com.electronwill.nightconfig.core.serde.*;

import java.awt.*;

public class NightDeserializers {
    public static final FloatingPointDeserializer FLOATING_POINT = new FloatingPointDeserializer();
    public static final ColorDeserializer COLOR = new ColorDeserializer();

    public static void provideToBuilder(ObjectDeserializerBuilder builder) {
        builder.withDeserializerProvider((valueClass, resultType) -> resultType.getSatisfyingRawType().map(resultClass -> {
            if (FloatingPointDeserializer.isNumberTypeSupported(valueClass) && TypeUtil.isPrimitiveOrWrapperNumber(resultClass)) {
                return FLOATING_POINT;
            }

            if (Color.class.isAssignableFrom(resultClass)) {
                return COLOR;
            }

            return null;
        }).orElse(null));
    }
}
