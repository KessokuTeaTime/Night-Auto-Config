package band.kessokuteatime.nightautoconfig.serde.serializers;

import com.electronwill.nightconfig.core.serde.SerializerContext;
import com.electronwill.nightconfig.core.serde.ValueSerializer;

import java.awt.*;

public class ColorToIntegerSerializer implements ValueSerializer<Color, Integer> {
    @Override
    public Integer serialize(Color value, SerializerContext ctx) {
        return value.getRGB() << 8 | value.getAlpha();
    }
}
