package band.kessokuteatime.nightautoconfig.serializer.base;

import band.kessokuteatime.nightautoconfig.serializer.NightConfigSerializer;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.hocon.HoconFormat;
import com.electronwill.nightconfig.json.JsonFormat;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.yaml.YamlFormat;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.util.Utils;

import java.nio.file.Path;

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

    public <T extends ConfigData> NightConfigSerializer<T> serializer(Config definition, Class<T> configClass) {
        return new NightConfigSerializer<>(definition, configClass, this, FileConfig.builder(getConfigPath(definition)));
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
        com.electronwill.nightconfig.core.Config config = switch (this) {
            case JSON -> JsonFormat.newConfig();
            case YAML -> YamlFormat.newConfig();
            case TOML -> TomlFormat.newConfig();
            case HOCON -> HoconFormat.newConfig();
        };

        if (object instanceof com.electronwill.nightconfig.core.Config oldConfig) {
            // Migrate entries
            config.addAll(oldConfig);
        } else {
            new ObjectConverter().toConfig(object, config);
        }

        return config;
    }

    public static final ConfigType DEFAULT_SIMPLE = JSON;
    public static final ConfigType DEFAULT_COMMENTED = TOML;
}
