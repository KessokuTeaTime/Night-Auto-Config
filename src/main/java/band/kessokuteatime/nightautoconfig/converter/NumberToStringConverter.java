package band.kessokuteatime.nightautoconfig.converter;

import com.electronwill.nightconfig.core.conversion.Converter;

import java.text.NumberFormat;
import java.text.ParseException;

public class NumberToStringConverter<N extends Number> implements Converter<N, String> {
    @Override
    public N convertToField(String value) {
        try {
            return (N) NumberFormat.getInstance().parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertFromField(N value) {
        return value.toString();
    }
}
