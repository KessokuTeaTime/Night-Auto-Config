package band.kessokuteatime.nightautoconfig.serializer;

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
        return Utils.getConfigFolder().resolve(definition.name() + suffix());
    }
}
