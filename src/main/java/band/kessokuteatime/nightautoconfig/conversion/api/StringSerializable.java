package band.kessokuteatime.nightautoconfig.conversion.api;

import com.electronwill.nightconfig.core.conversion.Converter;

public interface StringSerializable<T> {
    String convertToString(T value);
    T convertFromString(String value);

    interface WithConverter<T> extends StringSerializable<T>, Converter<T, String> {
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

    interface IdentityWithConverter extends Identity, WithConverter<String> {
        class Impl implements IdentityWithConverter {}
    }
}
