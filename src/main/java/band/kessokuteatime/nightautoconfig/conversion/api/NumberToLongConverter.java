package band.kessokuteatime.nightautoconfig.conversion.api;

import com.electronwill.nightconfig.core.conversion.Converter;

public interface NumberToLongConverter<N extends Number> extends Converter<N, Long> {
    interface FromInt extends NumberToLongConverter<Integer> {
        @Override
        default Integer convertToField(Long value) {
            return value.intValue();
        }

        @Override
        default Long convertFromField(Integer value) {
            return value.longValue();
        }

        class Impl implements FromInt {}
    }

    interface FromShort extends NumberToLongConverter<Short> {
        @Override
        default Short convertToField(Long value) {
            return value.shortValue();
        }

        @Override
        default Long convertFromField(Short value) {
            return value.longValue();
        }

        class Impl implements FromShort {}
    }

    interface FromByte extends NumberToLongConverter<Byte> {
        @Override
        default Byte convertToField(Long value) {
            return value.byteValue();
        }

        @Override
        default Long convertFromField(Byte value) {
            return value.longValue();
        }

        class Impl implements FromByte {
        }
    }
}
