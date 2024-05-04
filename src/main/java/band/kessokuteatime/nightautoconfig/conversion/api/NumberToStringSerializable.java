package band.kessokuteatime.nightautoconfig.conversion.api;

import java.text.NumberFormat;
import java.text.ParseException;

public interface NumberToStringSerializable<N extends Number> extends StringSerializable<N> {
    @Override
    default String convertToString(N value) {
        return value.toString();
    }

    @Override
    default N convertFromString(String value) {
        try {
            // Always parses to double for floating points or long for integers
            Number number = NumberFormat.getInstance().parse(value);
            return cast(number);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    N cast(Number number);

    class FromDouble implements NumberToStringSerializable<Double> {
        @Override
        public Double cast(Number number) {
            return number.doubleValue();
        }
    }

    class FromFloat implements NumberToStringSerializable<Float> {
        @Override
        public Float cast(Number number) {
            return number.floatValue();
        }
    }

    class FromInteger implements NumberToStringSerializable<Integer> {
        @Override
        public Integer cast(Number number) {
            return number.intValue();
        }
    }

    class FromLong implements NumberToStringSerializable<Long> {
        @Override
        public Long cast(Number number) {
            return number.longValue();
        }
    }

    class FromShort implements NumberToStringSerializable<Short> {
        @Override
        public Short cast(Number number) {
            return number.shortValue();
        }
    }

    class FromByte implements NumberToStringSerializable<Byte> {
        @Override
        public Byte cast(Number number) {
            return number.byteValue();
        }
    }
}
