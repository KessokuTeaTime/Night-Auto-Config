package band.kessokuteatime.nightautoconfig.converter;

import com.electronwill.nightconfig.core.conversion.Converter;

@Deprecated
public class FloatToDoubleConverter implements Converter<Float, Double> {
    @Override
    public Float convertToField(Double value) {
        return value.floatValue();
    }

    @Override
    public Double convertFromField(Float value) {
        return value.doubleValue();
    }
}
