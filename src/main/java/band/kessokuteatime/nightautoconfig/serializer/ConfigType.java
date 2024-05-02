package band.kessokuteatime.nightautoconfig.serializer;

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

    public Path getConfigPath(Config definition) {
        return Utils.getConfigFolder().resolve(definition.name() + "." + suffix());
    }

    public <T extends ConfigData> NightConfigSerializer<T, ?, ?, ?> serializer(Config definition, Class<T> configClass) {
        return switch (this) {
            case JSON -> new GeneralNightConfigSerializer<>(definition, configClass, this);
            case YAML, TOML, HOCON -> new CommentedNightConfigSerializer<>(definition, configClass, this);
        };
    }
}
