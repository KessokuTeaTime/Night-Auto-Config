package band.kessokuteatime.nightautoconfig.serde.serializers;

import com.electronwill.nightconfig.core.serde.*;

import java.awt.*;
import java.util.Map;

public class ColorToMapSerializer implements ValueSerializer<Color, Map<String, Integer>> {
    @Override
    public Map<String, Integer> serialize(Color value, SerializerContext ctx) {
        return Map.of(
                "red", value.getRed(),
                "blue", value.getBlue(),
                "green", value.getGreen(),
                "alpha", value.getAlpha()
        );
    }
}
