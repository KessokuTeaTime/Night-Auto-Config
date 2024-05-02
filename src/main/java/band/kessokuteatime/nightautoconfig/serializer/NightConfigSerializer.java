package band.kessokuteatime.nightautoconfig.serializer;

import band.kessokuteatime.nightautoconfig.spec.Specs;
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

    protected final Specs<T> specs;

    public NightConfigSerializer(Config definition, Class<T> configClass, ConfigType type, B builder) {
        this.definition = definition;
        this.configClass = configClass;
        this.type = type;
        this.builder = (B) builder.preserveInsertionOrder();

        this.specs = new Specs<>(createDefault(), type, type.getFileName(definition));
    }

    @Override
    public void serialize(T t) throws SerializationException {
        if (Files.exists(type.getConfigPath(definition))) {
            FileConf config = new ObjectConverter().toConfig(t, builder::build);

            specs.correct(config, Specs.Session.SAVING);
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

            config.load();
            specs.correct(config, Specs.Session.LOADING);
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
