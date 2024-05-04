package band.kessokuteatime.nightautoconfig.conversion.api;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Converter;

import java.util.HashMap;
import java.util.Map;

public interface MapToConfigConverter<K> extends Converter<Map<K, ?>, Config>, StringSerializable<K> {
    @Override
    default Map<K, ?> convertToField(Config value) {
        Map<K, ?> map = new HashMap<>();
        value.entrySet().forEach(entry -> map.put(convertFromString(entry.getKey()), entry.getValue()));

        return map;
    }

    @Override
    default Config convertFromField(Map<K, ?> value) {
        Config config = Config.inMemory();
        value.forEach((k, v) -> config.set(convertToString(k), v));

        return config;
    }

    interface StringKey extends MapToConfigConverter<String>, StringSerializable.Identity {
        class Impl implements StringKey {}
    }

    interface NumberKey<N extends Number> extends MapToConfigConverter<N>, NumberToStringSerializable<N> {
        interface DoubleKey extends NumberKey<Double>, NumberToStringSerializable.FromDouble {
            class Impl implements DoubleKey {}
        }

        interface FloatKey extends NumberKey<Float>, NumberToStringSerializable.FromFloat {
            class Impl implements FloatKey {}
        }

        interface LongKey extends NumberKey<Long>, NumberToStringSerializable.FromLong {
            class Impl implements LongKey {}
        }

        interface IntKey extends NumberKey<Integer>, NumberToStringSerializable.FromInteger {
            class Impl implements IntKey {}
        }

        interface ShortKey extends NumberKey<Short>, NumberToStringSerializable.FromShort {
            class Impl implements ShortKey {}
        }

        interface ByteKey extends NumberKey<Byte>, NumberToStringSerializable.FromByte {
            class Impl implements ByteKey {}
        }
    }
}
