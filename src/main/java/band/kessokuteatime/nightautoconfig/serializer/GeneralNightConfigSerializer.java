package band.kessokuteatime.nightautoconfig.serializer;

import band.kessokuteatime.nightautoconfig.serializer.base.ConfigType;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileConfigBuilder;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.function.UnaryOperator;

public class GeneralNightConfigSerializer<T extends ConfigData> extends NightConfigSerializer<T, com.electronwill.nightconfig.core.Config, FileConfig, FileConfigBuilder> {
    public GeneralNightConfigSerializer(Config definition, Class<T> configClass, ConfigType type, FileConfigBuilder builder) {
        super(definition, configClass, type, builder);
    }

    public GeneralNightConfigSerializer(Config definition, Class<T> configClass, ConfigType type, UnaryOperator<FileConfigBuilder> builder) {
        super(definition, configClass, type, builder.apply(FileConfig.builder(type.getConfigPath(definition))));
    }

    public GeneralNightConfigSerializer(Config definition, Class<T> configClass, ConfigType type) {
        this(definition, configClass, type, UnaryOperator.identity());
    }
}
