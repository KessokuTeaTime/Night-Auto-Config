package band.kessokuteatime.nightautoconfig.converter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberToStringConverterTest {
    @Test
    void convertToDoubleField() {
        NumberToStringConverter.Double doubleConverter = new NumberToStringConverter.Double();
        double value = 123.456;
        assertEquals(value, doubleConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertToFloatField() {
        NumberToStringConverter.Float floatConverter = new NumberToStringConverter.Float();
        float value = 123.456F;
        assertEquals(value, floatConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertToLongField() {
        NumberToStringConverter.Long longConverter = new NumberToStringConverter.Long();
        long value = 123456L;
        assertEquals(value, longConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertToIntegerField() {
        NumberToStringConverter.Integer integerConverter = new NumberToStringConverter.Integer();
        int value = 123456;
        assertEquals(value, integerConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertToShortField() {
        NumberToStringConverter.Short shortConverter = new NumberToStringConverter.Short();
        short value = 123;
        assertEquals(value, shortConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertToByteField() {
        NumberToStringConverter.Byte byteConverter = new NumberToStringConverter.Byte();
        byte value = 123;
        assertEquals(value, byteConverter.convertToField(String.valueOf(value)));
    }

    @Test
    void convertFromDoubleField() {
        NumberToStringConverter.Double doubleConverter = new NumberToStringConverter.Double();
        double value = 123.456;
        assertEquals(String.valueOf(value), doubleConverter.convertFromField(value));
    }

    @Test
    void convertFromFloatField() {
        NumberToStringConverter.Float floatConverter = new NumberToStringConverter.Float();
        float value = 123.456F;
        assertEquals(String.valueOf(value), floatConverter.convertFromField(value));
    }

    @Test
    void convertFromLongField() {
        NumberToStringConverter.Long longConverter = new NumberToStringConverter.Long();
        long value = 123456L;
        assertEquals(String.valueOf(value), longConverter.convertFromField(value));
    }

    @Test
    void convertFromIntegerField() {
        NumberToStringConverter.Integer integerConverter = new NumberToStringConverter.Integer();
        int value = 123456;
        assertEquals(String.valueOf(value), integerConverter.convertFromField(value));
    }

    @Test
    void convertFromShortField() {
        NumberToStringConverter.Short shortConverter = new NumberToStringConverter.Short();
        short value = 123;
        assertEquals(String.valueOf(value), shortConverter.convertFromField(value));
    }

    @Test
    void convertFromByteField() {
        NumberToStringConverter.Byte byteConverter = new NumberToStringConverter.Byte();
        byte value = 123;
        assertEquals(String.valueOf(value), byteConverter.convertFromField(value));
    }
}