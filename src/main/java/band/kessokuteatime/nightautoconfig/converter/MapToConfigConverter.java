package band.kessokuteatime.nightautoconfig.converter;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;

import java.util.HashMap;
import java.util.Map;

public class MapToConfigConverter implements Converter<Map<?, ?>, Config> {
    @Override
    public Map<?, ?> convertToField(Config value) {
        Map<?, ?> map = new HashMap<>();
        value.entrySet().forEach(entry -> {
            //map.put(entry.getKey(), entry.getValue());
        });

        return map;
    }

    @Override
    public Config convertFromField(Map<?, ?> value) {
        Config config = Config.inMemory();
        new ObjectConverter().toConfig(value, config);

        return config;
    }
}
