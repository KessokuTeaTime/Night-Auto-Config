package band.kessokuteatime.nightautoconfig.serializer;

import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfigBuilder;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.util.Utils;

import java.util.function.UnaryOperator;

public class NightConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {
    protected final Config definition;
    protected final Class<T> configClass;
    protected final ConfigType type;
    protected final CommentedFileConfigBuilder builder;

    public NightConfigSerializer(Config definition, Class<T> configClass, ConfigType type, CommentedFileConfigBuilder builder) {
        this.definition = definition;
        this.configClass = configClass;
        this.type = type;
        this.builder = builder;
    }

    public NightConfigSerializer(Config definition, Class<T> configClass, ConfigType type, UnaryOperator<CommentedFileConfigBuilder> builder) {
        this(definition, configClass, type, builder.apply(CommentedFileConfig.builder(type.getConfigPath(definition))));
    }

    public NightConfigSerializer(Config definition, Class<T> configClass, ConfigType type) {
        this(definition, configClass, type, UnaryOperator.identity());
    }

    @Override
    public void serialize(T t) throws SerializationException {
        CommentedFileConfig config = new ObjectConverter().toConfig(t, builder::build);
        config.save();
        config.close();
    }

    @Override
    public T deserialize() throws SerializationException {
        CommentedFileConfig config = builder.build();
        config.load();
        return new ObjectConverter().toObject(config, this::createDefault);
    }

    @Override
    public T createDefault() {
        return Utils.constructUnsafely(configClass);
    }
}
