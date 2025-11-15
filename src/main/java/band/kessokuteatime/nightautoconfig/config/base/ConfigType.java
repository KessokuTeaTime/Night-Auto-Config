package band.kessokuteatime.nightautoconfig.config.base;

import band.kessokuteatime.nightautoconfig.config.NightConfigSerializer;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.file.GenericBuilder;
import com.electronwill.nightconfig.hocon.HoconFormat;
import com.electronwill.nightconfig.json.JsonFormat;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.yaml.YamlFormat;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.util.Utils;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.function.UnaryOperator;

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

    public Path getRelativeConfigPath(Config definition) {
        return FMLPaths.CONFIGDIR.get().relativize(getConfigPath(definition));
    }

    public NightConfigSerializer.Builder builder() {
        return new NightConfigSerializer.Builder(this);
    }

    public <T extends ConfigData> ConfigSerializer.Factory<T> serializer(UnaryOperator<GenericBuilder<com.electronwill.nightconfig.core.Config, FileConfig>> genericBuilderConstructor) {
        return (definition, configClass) -> builder()
                .then(genericBuilderConstructor)
                .build(definition, configClass);
    }

    public <T extends ConfigData> NightConfigSerializer<T> defaultSerializer(Config definition, Class<T> configClass) {
        return builder()
                .then(GenericBuilder::preserveInsertionOrder)
                .build(definition, configClass);
    }

    public <T extends ConfigData> NightConfigSerializer<T> fileWatcherSerializer(Config definition, Class<T> configClass) {
        return builder()
                .then(GenericBuilder::preserveInsertionOrder)
                .then(GenericBuilder::autosave)
                .then(GenericBuilder::autoreload)
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

    public static final ConfigType DEFAULT_SIMPLE = JSON;
    public static final ConfigType DEFAULT_COMMENTED = TOML;
}
