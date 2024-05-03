package band.kessokuteatime.nightautoconfig.converter;

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

    interface Converter<N extends Number> extends NumberToStringSerializable<N>, StringSerializable.Converter<N> { }

    interface FromDouble extends NumberToStringSerializable<Double> {
        @Override
        default Double cast(Number number) {
            return number.doubleValue();
        }

        interface Converter extends NumberToStringSerializable.Converter<Double>, FromDouble {
            class Impl implements FromDouble.Converter {}
        }

        class Impl implements FromDouble {}
    }

    interface FromFloat extends NumberToStringSerializable<Float> {
        @Override
        default Float cast(Number number) {
            return number.floatValue();
        }

        interface Converter extends NumberToStringSerializable.Converter<Float>, FromFloat {
            class Impl implements FromFloat.Converter {}
        }

        class Impl implements FromFloat {}
    }

    interface FromInteger extends NumberToStringSerializable<Integer> {
        @Override
        default Integer cast(Number number) {
            return number.intValue();
        }

        interface Converter extends NumberToStringSerializable.Converter<Integer>, FromInteger {
            class Impl implements FromInteger.Converter {}
        }

        class Impl implements FromInteger {}
    }

    interface FromLong extends NumberToStringSerializable<Long> {
        @Override
        default Long cast(Number number) {
            return number.longValue();
        }

        interface Converter extends NumberToStringSerializable.Converter<Long>, FromLong {
            class Impl implements FromLong.Converter {}
        }

        class Impl implements FromLong {}
    }

    interface FromShort extends NumberToStringSerializable<Short> {
        @Override
        default Short cast(Number number) {
            return number.shortValue();
        }

        interface Converter extends NumberToStringSerializable.Converter<Short>, FromShort {
            class Impl implements FromShort.Converter {}
        }

        class Impl implements FromShort {}
    }

    interface FromByte extends NumberToStringSerializable<Byte> {
        @Override
        default Byte cast(Number number) {
            return number.byteValue();
        }

        interface Converter extends NumberToStringSerializable.Converter<Byte>, FromByte {
            class Impl implements FromByte.Converter {}
        }

        class Impl implements FromByte {}
    }
}
