package band.kessokuteatime.nightautoconfig.serializer;

import band.kessokuteatime.nightautoconfig.NightAutoConfig;
import band.kessokuteatime.nightautoconfig.serializer.base.ConfigType;
import band.kessokuteatime.nightautoconfig.spec.Specs;
import com.electronwill.nightconfig.core.conversion.ConversionTable;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileConfigBuilder;
import com.electronwill.nightconfig.core.file.GenericBuilder;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.util.Utils;

import java.io.IOException;
import java.nio.file.Files;

public class NightConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {
    protected final Config definition;
    protected final Class<T> configClass;
    protected final ConfigType type;
    protected final GenericBuilder<com.electronwill.nightconfig.core.Config, FileConfig> builder;

    protected final Specs<T> specs;
    protected final ConversionTable universalConfigConversionTable;

    public NightConfigSerializer(Config definition, Class<T> configClass, ConfigType type, FileConfigBuilder builder) {
        this.definition = definition;
        this.configClass = configClass;
        this.type = type;
        this.builder = builder.preserveInsertionOrder();

        this.specs = new Specs<>(createDefault(), type, type.getFileName(definition));

        this.universalConfigConversionTable = new ConversionTable();
        universalConfigConversionTable.put(com.electronwill.nightconfig.core.Config.class, ConfigType.DEFAULT_SIMPLE::wrap);
    }

    @Override
    public void serialize(T t) throws SerializationException {
        if (Files.exists(type.getConfigPath(definition))) {
            FileConfig config = new ObjectConverter().toConfig(t, builder::build);

            NightAutoConfig.normalize(config);
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
            FileConfig config = builder.build();
            config.load();

            NightAutoConfig.normalize(config);
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
