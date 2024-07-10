package band.kessokuteatime.nightautoconfig.serde;


import band.kessokuteatime.nightautoconfig.serde.serializers.ColorToIntegerSerializer;
import band.kessokuteatime.nightautoconfig.serde.serializers.ColorToMapSerializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializerBuilder;

import java.awt.*;

public class NightSerializers {
    public static final ColorToIntegerSerializer COLOR_TO_INTEGER = new ColorToIntegerSerializer();
    public static final ColorToMapSerializer COLOR_TO_MAP = new ColorToMapSerializer();

    public static void provideToBuilder(ObjectSerializerBuilder builder) {
        builder.withSerializerProvider((valueClass, ctx) -> {
            if (Color.class.isAssignableFrom(valueClass)) {
                return COLOR_TO_INTEGER;
            }

            return null;
        });
    }
}
