package band.kessokuteatime.nightautoconfig.converter;

import com.electronwill.nightconfig.core.conversion.Converter;

public class StringIdentityConverter implements Converter<String, String> {
    @Override
    public String convertToField(String value) {
        return value;
    }

    @Override
    public String convertFromField(String value) {
        return value;
    }
}
