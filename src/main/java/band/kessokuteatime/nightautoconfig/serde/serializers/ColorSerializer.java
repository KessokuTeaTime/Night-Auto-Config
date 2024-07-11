package band.kessokuteatime.nightautoconfig.serde.serializers;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.serde.*;

import java.awt.*;
import java.util.Map;

public class ColorSerializer implements ValueSerializer<Color, Config> {
    @Override
    public Config serialize(Color value, SerializerContext ctx) {
        Config config = ctx.createConfig();
        config.set("red", value.getRed());
        config.set("green", value.getGreen());
        config.set("blue", value.getBlue());
        config.set("alpha", value.getAlpha());
        return config;
    }
}
