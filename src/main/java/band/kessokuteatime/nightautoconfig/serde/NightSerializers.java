package band.kessokuteatime.nightautoconfig.serde;

import band.kessokuteatime.nightautoconfig.serde.serializers.ColorSerializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializerBuilder;

import java.awt.*;

public class NightSerializers {
    public static final ColorSerializer COLOR = new ColorSerializer();

    public static void provideToBuilder(ObjectSerializerBuilder builder) {
        builder.withSerializerProvider((valueClass, ctx) -> {
            if (Color.class.isAssignableFrom(valueClass)) {
                return COLOR;
            }

            return null;
        });
    }
}
