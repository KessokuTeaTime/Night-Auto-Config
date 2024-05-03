package band.kessokuteatime.nightautoconfig.converter;

import com.electronwill.nightconfig.core.conversion.Converter;

import java.text.NumberFormat;
import java.text.ParseException;

public abstract class NumberToStringConverter<N extends Number> implements Converter<N, String> {
    @Override
    public N convertToField(String value) {
        try {
            // Always parses to double for floating points or long for integers
            Number number = NumberFormat.getInstance().parse(value);
            return cast(number);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertFromField(N value) {
        return value.toString();
    }

    protected abstract N cast(Number number);

    public static class Double extends NumberToStringConverter<java.lang.Double> {
        @Override
        protected java.lang.Double cast(Number number) {
            return number.doubleValue();
        }
    }

    public static class Float extends NumberToStringConverter<java.lang.Float> {
        @Override
        protected java.lang.Float cast(Number number) {
            return number.floatValue();
        }
    }

    public static class Integer extends NumberToStringConverter<java.lang.Integer> {
        @Override
        protected java.lang.Integer cast(Number number) {
            return number.intValue();
        }
    }

    public static class Long extends NumberToStringConverter<java.lang.Long> {
        @Override
        protected java.lang.Long cast(Number number) {
            return number.longValue();
        }
    }

    public static class Short extends NumberToStringConverter<java.lang.Short> {
        @Override
        protected java.lang.Short cast(Number number) {
            return number.shortValue();
        }
    }

    public static class Byte extends NumberToStringConverter<java.lang.Byte> {
        @Override
        protected java.lang.Byte cast(Number number) {
            return number.byteValue();
        }
    }
}
