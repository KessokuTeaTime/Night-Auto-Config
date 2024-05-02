package band.kessokuteatime.nightautoconfig.serializer;

import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileConfigBuilder;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.util.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.function.UnaryOperator;

public class NightConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {
    protected final Config definition;
    protected final Class<T> configClass;
    protected final ConfigType type;
    protected final FileConfigBuilder builder;

    public NightConfigSerializer(Config definition, Class<T> configClass, ConfigType type, FileConfigBuilder builder) {
        this.definition = definition;
        this.configClass = configClass;
        this.type = type;
        this.builder = builder;
    }

    public NightConfigSerializer(Config definition, Class<T> configClass, ConfigType type, UnaryOperator<FileConfigBuilder> builder) {
        this(definition, configClass, type, builder.apply(FileConfig.builder(type.getConfigPath(definition))));
    }

    public NightConfigSerializer(Config definition, Class<T> configClass, ConfigType type) {
        this(definition, configClass, type, UnaryOperator.identity());
    }

    @Override
    public void serialize(T t) throws SerializationException {
        if (Files.exists(type.getConfigPath(definition))) {
            FileConfig config = new ObjectConverter().toConfig(t, builder::build);
            config.save();
            config.close();
        } else {
            try {
                Files.createFile(type.getConfigPath(definition));
                serialize(t);
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }
    }

    @Override
    public T deserialize() throws SerializationException {
        if (Files.exists(type.getConfigPath(definition))) {
            FileConfig config = builder.build();
            config.load();
            return new ObjectConverter().toObject(config, this::createDefault);
        } else {
            T t = createDefault();
            serialize(t);
            return t;
        }
    }

    @Override
    public T createDefault() {
        return Utils.constructUnsafely(configClass);
    }
}
