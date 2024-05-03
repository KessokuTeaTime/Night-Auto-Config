package band.kessokuteatime.nightautoconfig.converter;

public interface StringSerializable<T> {
    String convertToString(T value);
    T convertFromString(String value);

    interface Converter<T> extends StringSerializable<T>, com.electronwill.nightconfig.core.conversion.Converter<T, String> {
        @Override
        default T convertToField(String value) {
            return convertFromString(value);
        }

        @Override
        default String convertFromField(T value) {
            return convertToString(value);
        }
    }

    interface Identity extends StringSerializable<String> {
        @Override
        default String convertToString(String value) {
            return value;
        }

        @Override
        default String convertFromString(String value) {
            return value;
        }

        class Impl implements Identity {}
    }

    interface IdentityConverter extends Identity, Converter<String> {
        class Impl implements IdentityConverter {}
    }
}
