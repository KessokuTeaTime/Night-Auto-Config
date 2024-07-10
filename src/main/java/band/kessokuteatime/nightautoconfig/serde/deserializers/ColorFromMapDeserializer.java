package band.kessokuteatime.nightautoconfig.serde.deserializers;

import com.electronwill.nightconfig.core.serde.DeserializerContext;
import com.electronwill.nightconfig.core.serde.TypeConstraint;
import com.electronwill.nightconfig.core.serde.ValueDeserializer;

import java.awt.*;
import java.util.Map;
import java.util.Optional;

public class ColorFromMapDeserializer implements ValueDeserializer<Map<String, Integer>, Color> {
    @Override
    public Color deserialize(Map<String, Integer> value, Optional<TypeConstraint> resultType, DeserializerContext ctx) {
        int red = value.getOrDefault("red", 0);
        int green = value.getOrDefault("green", 0);
        int blue = value.getOrDefault("blue", 0);
        int alpha = value.getOrDefault("alpha", 255);

        return new Color(red, green, blue, alpha);
    }
}
