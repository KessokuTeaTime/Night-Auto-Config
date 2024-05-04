package band.kessokuteatime.nightautoconfig.conversion.api;

import com.electronwill.nightconfig.core.conversion.Converter;

public interface StringSerializable<T> {
    String convertToString(T value);
    T convertFromString(String value);

    class Identity implements StringSerializable<String> {
        @Override
        public String convertToString(String value) {
            return value;
        }

        @Override
        public String convertFromString(String value) {
            return value;
        }
    }

    class ObjectIdentity implements StringSerializable<Object> {
        @Override
        public String convertToString(Object value) {
            return value.toString();
        }

        @Override
        public Object convertFromString(String value) {
            return value;
        }
    }
}
