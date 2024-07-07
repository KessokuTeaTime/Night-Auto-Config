package band.kessokuteatime.nightautoconfig.serde.deserializer;

import com.electronwill.nightconfig.core.serde.DeserializerContext;
import com.electronwill.nightconfig.core.serde.TypeConstraint;
import com.electronwill.nightconfig.core.serde.ValueDeserializer;

import java.util.Optional;

public class RiskyFloatingPointDeserializer implements ValueDeserializer<Number, Number> {
    public static boolean isNumberTypeSupported(Class<?> t) {
        return t == Float.class || t == float.class || t == Double.class || t == double.class;
    }

    @Override
    public Number deserialize(Number value, Optional<TypeConstraint> resultType, DeserializerContext ctx) {
        TypeConstraint numberType = resultType.orElseThrow(() -> new RuntimeException(
                "Cannot deserialize a value with a risky number conversion without knowing the number type"
        ));
        Class<?> resultCls = numberType.getSatisfyingRawType()
                .orElseThrow(() -> new RuntimeException(
                        "Could not find a concrete number type that can satisfy the constraint " + numberType
                ));
        Class<?> valueCls = value.getClass();

        if (valueCls == Double.class) {
            double d = value.doubleValue();
            if (resultCls == Float.class || resultCls == float.class) {
                // double to float
                float f = (float) d;
                if (((double) f) == d) {
                    return f;
                }
            } else {
                throw new RuntimeException(String.format(
                        "Cannot deserialize from %s to %s: risky conversion not implemented, you should change your types.",
                        valueCls, resultCls
                ));
            }
        } else if (valueCls == Float.class) {
            float f = value.floatValue();
            if (resultCls == Double.class || resultCls == double.class) {
                // float to double
                double d = (double) f;
                if (((float) d) == f) {
                    return d;
                }
            } else {
                throw new RuntimeException(String.format(
                        "Cannot deserialize from %s to %s: risky conversion not implemented, you should change your types.",
                        valueCls, resultCls
                ));
            }
        }
        throw new RuntimeException(String.format(
                "Cannot deserialize %s to %s: the conversion would be lossy", value, resultCls
        ));
    }
}
