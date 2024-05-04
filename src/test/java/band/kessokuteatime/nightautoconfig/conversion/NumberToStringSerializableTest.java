package band.kessokuteatime.nightautoconfig.conversion;

import band.kessokuteatime.nightautoconfig.conversion.api.NumberToStringSerializable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberToStringSerializableTest {
    @Test
    void convertToDoubleField() {
        NumberToStringSerializable.WithConverter<Double> fromDoubleWithConverter = new NumberToStringSerializable.FromDouble.WithConverter.Impl();
        double value = 123.456;
        assertEquals(value, fromDoubleWithConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertToFloatField() {
        NumberToStringSerializable.WithConverter<Float> fromFloatWithConverter = new NumberToStringSerializable.FromFloat.WithConverter.Impl();
        float value = 123.456F;
        assertEquals(value, fromFloatWithConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertToLongField() {
        NumberToStringSerializable.WithConverter<Long> fromLongWithConverter = new NumberToStringSerializable.FromLong.WithConverter.Impl();
        long value = 123456L;
        assertEquals(value, fromLongWithConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertToIntegerField() {
        NumberToStringSerializable.WithConverter<Integer> fromIntegerWithConverter = new NumberToStringSerializable.FromInteger.WithConverter.Impl();
        int value = 123456;
        assertEquals(value, fromIntegerWithConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertToShortField() {
        NumberToStringSerializable.WithConverter<Short> fromShortWithConverter = new NumberToStringSerializable.FromShort.WithConverter.Impl();
        short value = 123;
        assertEquals(value, fromShortWithConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertToByteField() {
        NumberToStringSerializable.WithConverter<Byte> fromByteWithConverter = new NumberToStringSerializable.FromByte.WithConverter.Impl();
        byte value = 123;
        assertEquals(value, fromByteWithConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertFromDoubleField() {
        NumberToStringSerializable.WithConverter<Double> fromDoubleWithConverter = new NumberToStringSerializable.FromDouble.WithConverter.Impl();
        double value = 123.456;
        assertEquals(String.valueOf(value), fromDoubleWithConverter.convertFromField(value));
    }

    @Test
    void convertFromFloatField() {
        NumberToStringSerializable.WithConverter<Float> fromFloatWithConverter = new NumberToStringSerializable.FromFloat.WithConverter.Impl();
        float value = 123.456F;
        assertEquals(String.valueOf(value), fromFloatWithConverter.convertFromField(value));
    }

    @Test
    void convertFromLongField() {
        NumberToStringSerializable.WithConverter<Long> fromLongWithConverter = new NumberToStringSerializable.FromLong.WithConverter.Impl();
        long value = 123456L;
        assertEquals(String.valueOf(value), fromLongWithConverter.convertFromField(value));
    }

    @Test
    void convertFromIntegerField() {
        NumberToStringSerializable.WithConverter<Integer> fromIntegerWithConverter = new NumberToStringSerializable.FromInteger.WithConverter.Impl();
        int value = 123456;
        assertEquals(String.valueOf(value), fromIntegerWithConverter.convertFromField(value));
    }

    @Test
    void convertFromShortField() {
        NumberToStringSerializable.WithConverter<Short> fromShortWithConverter = new NumberToStringSerializable.FromShort.WithConverter.Impl();
        short value = 123;
        assertEquals(String.valueOf(value), fromShortWithConverter.convertFromField(value));
    }

    @Test
    void convertFromByteField() {
        NumberToStringSerializable.WithConverter<Byte> fromByteWithConverter = new NumberToStringSerializable.FromByte.WithConverter.Impl();
        byte value = 123;
        assertEquals(String.valueOf(value), fromByteWithConverter.convertFromField(value));
    }
}