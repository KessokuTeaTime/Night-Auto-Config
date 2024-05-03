package band.kessokuteatime.nightautoconfig.converter;

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

    }
}
