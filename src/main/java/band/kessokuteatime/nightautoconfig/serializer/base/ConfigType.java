package band.kessokuteatime.nightautoconfig.serializer.base;

import band.kessokuteatime.nightautoconfig.serializer.CommentedNightConfigSerializer;
import band.kessokuteatime.nightautoconfig.serializer.GeneralNightConfigSerializer;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
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

    public <T extends ConfigData> NightConfigSerializer<T, ?, ?, ?> serializer(Config definition, Class<T> configClass) {
        return switch (this) {
            case JSON -> new GeneralNightConfigSerializer<>(definition, configClass, this);
            case YAML, TOML, HOCON -> new CommentedNightConfigSerializer<>(definition, configClass, this);
        };
    }

    public com.electronwill.nightconfig.core.Config wrap(Object object) {
        com.electronwill.nightconfig.core.Config config = switch (this) {
            case JSON -> JsonFormat.newConfig();
            case YAML -> YamlFormat.newConfig();
            case TOML -> TomlFormat.newConfig();
            case HOCON -> HoconFormat.newConfig();
        };
        new ObjectConverter().toConfig(object, config);
        return config;
    }
}
