package band.kessokuteatime.nightautoconfig.serializer.base;

import band.kessokuteatime.nightautoconfig.serializer.NightConfigSerializer;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.GenericBuilder;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;
import com.electronwill.nightconfig.hocon.HoconFormat;
import com.electronwill.nightconfig.json.JsonFormat;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.yaml.YamlFormat;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.util.Utils;

import java.nio.file.Path;
import java.util.function.Supplier;

public enum ConfigType {
    JSON("json"), YAML("yaml"), TOML("toml"), HOCON("conf");

    private final String suffix;

    ConfigType(String suffix) {
        this.suffix = suffix;
    }

    public String suffix() {
        return suffix;
    }

    public String getFileName(Config definition) {
        return definition.name() + "." + suffix();
    }

    public Path getConfigPath(Config definition) {
        return Utils.getConfigFolder().resolve(getFileName(definition));
    }

    public NightConfigSerializer.Builder builder() {
        return new NightConfigSerializer.Builder(this);
    }

    public <T extends ConfigData> NightConfigSerializer<T> defaultSerializer(Config definition, Class<T> configClass) {
        return builder()
                .then(GenericBuilder::preserveInsertionOrder)
                .build(definition, configClass);
    }

    public ConfigFormat<?> format() {
        return switch (this) {
            case JSON -> JsonFormat.fancyInstance();
            case YAML -> YamlFormat.defaultInstance();
            case TOML -> TomlFormat.instance();
            case HOCON -> HoconFormat.instance();
        };
    }

    public com.electronwill.nightconfig.core.Config wrap(Object object) {
        Supplier<com.electronwill.nightconfig.core.Config> supplier = () -> switch (this) {
            case JSON -> JsonFormat.newConfig();
            case YAML -> YamlFormat.newConfig();
            case TOML -> TomlFormat.newConfig();
            case HOCON -> HoconFormat.newConfig();
        };

        if (object instanceof com.electronwill.nightconfig.core.Config oldConfig) {
            // Migrate entries
            var config = supplier.get();
            config.addAll(oldConfig);
            return config;
        } else {
            return ObjectSerializer.standard().serializeFields(object, supplier);
        }
    }

    public static final ConfigType DEFAULT_SIMPLE = JSON;
    public static final ConfigType DEFAULT_COMMENTED = TOML;
}
