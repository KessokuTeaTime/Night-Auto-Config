package band.kessokuteatime.nightautoconfig.serde.deserializers;

import com.electronwill.nightconfig.core.serde.DeserializerContext;
import com.electronwill.nightconfig.core.serde.TypeConstraint;
import com.electronwill.nightconfig.core.serde.ValueDeserializer;

import java.awt.*;
import java.util.Optional;

public class ColorFromIntegerDeserializer implements ValueDeserializer<Integer, Color> {
    @Override
    public Color deserialize(Integer value, Optional<TypeConstraint> resultType, DeserializerContext ctx) {
        boolean hasAlpha = (value & 0x000000ffL) != 0;
        return new Color(value, hasAlpha);
    }
}
