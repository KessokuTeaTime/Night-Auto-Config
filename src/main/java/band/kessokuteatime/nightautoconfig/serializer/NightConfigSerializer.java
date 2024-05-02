package band.kessokuteatime.nightautoconfig.serializer;

import band.kessokuteatime.nightautoconfig.spec.SpecBuilder;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.GenericBuilder;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.util.Utils;

import java.io.IOException;
import java.nio.file.Files;

public abstract class NightConfigSerializer<
        T extends ConfigData,
        Conf extends com.electronwill.nightconfig.core.Config,
        FileConf extends FileConfig,
        B extends GenericBuilder<Conf, FileConf>
        > implements ConfigSerializer<T> {
    protected final Config definition;
    protected final Class<T> configClass;
    protected final ConfigType type;
    protected final B builder;

    public NightConfigSerializer(Config definition, Class<T> configClass, ConfigType type, B builder) {
        this.definition = definition;
        this.configClass = configClass;
        this.type = type;
        this.builder = builder;
    }

    @Override
    public void serialize(T t) throws SerializationException {
        if (Files.exists(type.getConfigPath(definition))) {
            FileConf config = new ObjectConverter().toConfig(t, builder::build);
            ConfigSpec spec = new SpecBuilder<>(t).build();

            spec.correct(config);
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
            FileConf config = builder.build();
            ConfigSpec spec = new SpecBuilder<>(createDefault()).build();

            config.load();
            spec.correct(config);
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
