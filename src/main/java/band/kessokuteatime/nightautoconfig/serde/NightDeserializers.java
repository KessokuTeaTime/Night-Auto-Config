package band.kessokuteatime.nightautoconfig.serde;

import band.kessokuteatime.nightautoconfig.serde.deserializers.ColorFromIntegerDeserializer;
import band.kessokuteatime.nightautoconfig.serde.deserializers.ColorFromMapDeserializer;
import band.kessokuteatime.nightautoconfig.serde.deserializers.FloatingPointDeserializer;
import band.kessokuteatime.nightautoconfig.util.TypeUtil;
import com.electronwill.nightconfig.core.serde.*;

import java.awt.*;
import java.util.Map;

public class NightDeserializers {
    public static final FloatingPointDeserializer FLOATING_POINT = new FloatingPointDeserializer();
    public static final ColorFromIntegerDeserializer COLOR_FROM_INTEGER = new ColorFromIntegerDeserializer();
    public static final ColorFromMapDeserializer COLOR_FROM_MAP = new ColorFromMapDeserializer();

    public static void provideToBuilder(ObjectDeserializerBuilder builder) {
        builder.withDeserializerProvider((valueClass, resultType) -> resultType.getSatisfyingRawType().map(resultClass -> {
            if (FloatingPointDeserializer.isNumberTypeSupported(valueClass) && TypeUtil.isPrimitiveOrWrapperNumber(resultClass)) {
                return FLOATING_POINT;
            }

            if ((valueClass == Integer.class || valueClass == int.class) && Color.class.isAssignableFrom(resultClass)) {
                return COLOR_FROM_INTEGER;
            }

            if (Map.class.isAssignableFrom(valueClass) && Color.class.isAssignableFrom(resultClass)) {
                return COLOR_FROM_MAP;
            }

            return null;
        }).orElse(null));
    }
}
