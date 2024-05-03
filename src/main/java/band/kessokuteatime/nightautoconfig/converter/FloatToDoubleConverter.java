package band.kessokuteatime.nightautoconfig.converter;

import com.electronwill.nightconfig.core.conversion.Converter;

public interface FloatToDoubleConverter extends Converter<Float, Double> {
    @Override
    default Float convertToField(Double value) {
        return value.floatValue();
    }

    @Override
    default Double convertFromField(Float value) {
        // This prevents precision loss caused by Float.doubleValue()
        // https://stackoverflow.com/a/41856558/23452915
        return Double.valueOf(value.toString());
    }

    class Impl implements FloatToDoubleConverter {}
}
