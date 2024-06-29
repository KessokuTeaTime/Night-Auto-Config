package band.kessokuteatime.nightautoconfig.serializer;

import band.kessokuteatime.nightautoconfig.conversion.NightConverter;
import band.kessokuteatime.nightautoconfig.serializer.base.ConfigType;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.ConversionTable;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.GenericBuilder;
import com.electronwill.nightconfig.core.serde.ObjectDeserializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.util.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.UnaryOperator;

public class NightConfigTemporarySerializer<T extends ConfigData> implements ConfigSerializer<T> {
    protected final me.shedaniel.autoconfig.annotation.Config definition;
    protected final Class<T> configClass;
    protected final ConfigType type;
    protected final GenericBuilder<Config, FileConfig> builder;

    public record Builder(ConfigType type, UnaryOperator<GenericBuilder<Config, FileConfig>> builder) {
        public Builder(ConfigType type) {
            this(type, UnaryOperator.identity());
        }

        public Builder type(ConfigType type) {
            return new Builder(type, builder);
        }

        public Builder builder(UnaryOperator<GenericBuilder<Config, FileConfig>> builder) {
            return new Builder(type, builder);
        }

        public Builder then(UnaryOperator<GenericBuilder<Config, FileConfig>> then) {
            return new Builder(type, b -> then.apply(builder.apply(b)));
        }

        public <T extends ConfigData> NightConfigTemporarySerializer<T> build(
                me.shedaniel.autoconfig.annotation.Config definition, Class<T> configClass
        ) {
            return new NightConfigTemporarySerializer<>(definition, configClass, type, builder.apply(FileConfig.builder(type.getConfigPath(definition))));
        }
    }

    public NightConfigTemporarySerializer(
            me.shedaniel.autoconfig.annotation.Config definition,
            Class<T> configClass, ConfigType type, GenericBuilder<Config, FileConfig> builder
    ) {
        this.definition = definition;
        this.configClass = configClass;
        this.type = type;
        this.builder = builder.preserveInsertionOrder();
    }

    @Override
    public void serialize(T t) throws SerializationException {
        Path path = type.getConfigPath(definition);
        if (Files.exists(path)) {
            FileConfig config = ObjectSerializer.standard().serializeFields(t, builder::build);

            //NightAutoConfig.normalize(config);
            //spec.correct(config);

            config.save();
            config.close();
        } else {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);

                serialize(t);
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }
    }

    @Override
    public T deserialize() throws SerializationException {
        Path path = type.getConfigPath(definition);
        if (Files.exists(path)) {
            FileConfig config = builder.build();
            config.load();

            return ObjectDeserializer.standard().deserializeFields(config, this::createDefault);
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
