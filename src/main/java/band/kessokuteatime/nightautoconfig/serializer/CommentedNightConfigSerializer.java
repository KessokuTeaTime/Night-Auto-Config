package band.kessokuteatime.nightautoconfig.serializer;

import band.kessokuteatime.nightautoconfig.serializer.base.ConfigType;
import band.kessokuteatime.nightautoconfig.serializer.base.NightConfigSerializer;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfigBuilder;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.function.UnaryOperator;

public class CommentedNightConfigSerializer<T extends ConfigData> extends NightConfigSerializer<T, CommentedConfig, CommentedFileConfig, CommentedFileConfigBuilder> {
    public CommentedNightConfigSerializer(Config definition, Class<T> configClass, ConfigType type, CommentedFileConfigBuilder builder) {
        super(definition, configClass, type, builder);
    }

    public CommentedNightConfigSerializer(Config definition, Class<T> configClass, ConfigType type, UnaryOperator<CommentedFileConfigBuilder> builder) {
        super(definition, configClass, type, builder.apply(CommentedFileConfig.builder(type.getConfigPath(definition))));
    }

    public CommentedNightConfigSerializer(Config definition, Class<T> configClass, ConfigType type) {
        this(definition, configClass, type, UnaryOperator.identity());
    }
}
