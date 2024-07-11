package band.kessokuteatime.nightautoconfig.serde.deserializers;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.serde.DeserializerContext;
import com.electronwill.nightconfig.core.serde.TypeConstraint;
import com.electronwill.nightconfig.core.serde.ValueDeserializer;

import java.awt.*;
import java.util.Optional;

public class ColorDeserializer implements ValueDeserializer<Config, Color> {
    @Override
    public Color deserialize(Config value, Optional<TypeConstraint> resultType, DeserializerContext ctx) {
        int red = value.get("red");
        int green = value.get("green");
        int blue = value.get("blue");
        int alpha = value.get("alpha");
        return new Color(red, green, blue, alpha);
    }
}
