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

    interface WithConverter<N extends Number> extends NumberToStringSerializable<N>, StringSerializable.WithConverter<N> { }

    interface FromDouble extends NumberToStringSerializable<Double> {
        @Override
        default Double cast(Number number) {
            return number.doubleValue();
        }

        interface WithConverter extends NumberToStringSerializable.WithConverter<Double>, FromDouble {
            class Impl implements FromDouble.WithConverter {}
        }

        class Impl implements FromDouble {}
    }

    interface FromFloat extends NumberToStringSerializable<Float> {
        @Override
        default Float cast(Number number) {
            return number.floatValue();
        }

        interface WithConverter extends NumberToStringSerializable.WithConverter<Float>, FromFloat {
            class Impl implements FromFloat.WithConverter {}
        }

        class Impl implements FromFloat {}
    }

    interface FromInteger extends NumberToStringSerializable<Integer> {
        @Override
        default Integer cast(Number number) {
            return number.intValue();
        }

        interface WithConverter extends NumberToStringSerializable.WithConverter<Integer>, FromInteger {
            class Impl implements FromInteger.WithConverter {}
        }

        class Impl implements FromInteger {}
    }

    interface FromLong extends NumberToStringSerializable<Long> {
        @Override
        default Long cast(Number number) {
            return number.longValue();
        }

        interface WithConverter extends NumberToStringSerializable.WithConverter<Long>, FromLong {
            class Impl implements FromLong.WithConverter {}
        }

        class Impl implements FromLong {}
    }

    interface FromShort extends NumberToStringSerializable<Short> {
        @Override
        default Short cast(Number number) {
            return number.shortValue();
        }

        interface WithConverter extends NumberToStringSerializable.WithConverter<Short>, FromShort {
            class Impl implements FromShort.WithConverter {}
        }

        class Impl implements FromShort {}
    }

    interface FromByte extends NumberToStringSerializable<Byte> {
        @Override
        default Byte cast(Number number) {
            return number.byteValue();
        }

        interface WithConverter extends NumberToStringSerializable.WithConverter<Byte>, FromByte {
            class Impl implements FromByte.WithConverter {}
        }

        class Impl implements FromByte {}
    }
}
