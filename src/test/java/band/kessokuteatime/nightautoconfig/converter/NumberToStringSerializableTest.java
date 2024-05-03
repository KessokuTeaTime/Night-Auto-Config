package band.kessokuteatime.nightautoconfig.converter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberToStringSerializableTest {
    @Test
    void convertToDoubleField() {
        NumberToStringSerializable.Converter<Double> fromDoubleConverter = new NumberToStringSerializable.FromDouble.Converter.Impl();
        double value = 123.456;
        assertEquals(value, fromDoubleConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertToFloatField() {
        NumberToStringSerializable.Converter<Float> fromFloatConverter = new NumberToStringSerializable.FromFloat.Converter.Impl();
        float value = 123.456F;
        assertEquals(value, fromFloatConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertToLongField() {
        NumberToStringSerializable.Converter<Long> fromLongConverter = new NumberToStringSerializable.FromLong.Converter.Impl();
        long value = 123456L;
        assertEquals(value, fromLongConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertToIntegerField() {
        NumberToStringSerializable.Converter<Integer> fromIntegerConverter = new NumberToStringSerializable.FromInteger.Converter.Impl();
        int value = 123456;
        assertEquals(value, fromIntegerConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertToShortField() {
        NumberToStringSerializable.Converter<Short> fromShortConverter = new NumberToStringSerializable.FromShort.Converter.Impl();
        short value = 123;
        assertEquals(value, fromShortConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertToByteField() {
        NumberToStringSerializable.Converter<Byte> fromByteConverter = new NumberToStringSerializable.FromByte.Converter.Impl();
        byte value = 123;
        assertEquals(value, fromByteConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertFromDoubleField() {
        NumberToStringSerializable.Converter<Double> fromDoubleConverter = new NumberToStringSerializable.FromDouble.Converter.Impl();
        double value = 123.456;
        assertEquals(String.valueOf(value), fromDoubleConverter.convertFromField(value));
    }

    @Test
    void convertFromFloatField() {
        NumberToStringSerializable.Converter<Float> fromFloatConverter = new NumberToStringSerializable.FromFloat.Converter.Impl();
        float value = 123.456F;
        assertEquals(String.valueOf(value), fromFloatConverter.convertFromField(value));
    }

    @Test
    void convertFromLongField() {
        NumberToStringSerializable.Converter<Long> fromLongConverter = new NumberToStringSerializable.FromLong.Converter.Impl();
        long value = 123456L;
        assertEquals(String.valueOf(value), fromLongConverter.convertFromField(value));
    }

    @Test
    void convertFromIntegerField() {
        NumberToStringSerializable.Converter<Integer> fromIntegerConverter = new NumberToStringSerializable.FromInteger.Converter.Impl();
        int value = 123456;
        assertEquals(String.valueOf(value), fromIntegerConverter.convertFromField(value));
    }

    @Test
    void convertFromShortField() {
        NumberToStringSerializable.Converter<Short> fromShortConverter = new NumberToStringSerializable.FromShort.Converter.Impl();
        short value = 123;
        assertEquals(String.valueOf(value), fromShortConverter.convertFromField(value));
    }

    @Test
    void convertFromByteField() {
        NumberToStringSerializable.Converter<Byte> fromByteConverter = new NumberToStringSerializable.FromByte.Converter.Impl();
        byte value = 123;
        assertEquals(String.valueOf(value), fromByteConverter.convertFromField(value));
    }
}