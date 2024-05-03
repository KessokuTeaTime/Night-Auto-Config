package band.kessokuteatime.nightautoconfig.converter;

import com.electronwill.nightconfig.core.conversion.Converter;

public interface FloatToDoubleConverter extends Converter<Float, Double> {
    @Override
    default Float convertToField(Double value) {
        return value.floatValue();
    }

    @Override
    default Double convertFromField(Float value) {
        return value.doubleValue();
    }

    class Impl implements FloatToDoubleConverter {}
}
