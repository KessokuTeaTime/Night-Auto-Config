package band.kessokuteatime.nightautoconfig.config;

import band.kessokuteatime.nightautoconfig.config.base.ConfigType;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.GenericBuilder;
import com.electronwill.nightconfig.core.serde.ObjectDeserializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.util.Utils;

import java.util.function.UnaryOperator;

public class NightConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {
    protected final me.shedaniel.autoconfig.annotation.Config definition;
    protected final Class<T> configClass;
    protected final ConfigType type;
    protected final GenericBuilder<Config, FileConfig> builder;

    public record Builder(ConfigType type, UnaryOperator<GenericBuilder<Config, FileConfig>> genericBuilder) {
        public Builder(ConfigType type) {
            this(type, UnaryOperator.identity());
        }

        public Builder type(ConfigType type) {
            return new Builder(type, genericBuilder);
        }

        public Builder then(UnaryOperator<GenericBuilder<Config, FileConfig>> then) {
            return new Builder(type, b -> then.apply(genericBuilder.apply(b)));
        }

        public <T extends ConfigData> NightConfigSerializer<T> build(
                me.shedaniel.autoconfig.annotation.Config definition, Class<T> configClass
        ) {
            return new NightConfigSerializer<>(
                    definition, configClass, type,
                    genericBuilder.apply(FileConfig.builder(type.getConfigPath(definition)))
            );
        }
    }

    public NightConfigSerializer(
            me.shedaniel.autoconfig.annotation.Config definition,
            Class<T> configClass, ConfigType type, GenericBuilder<Config, FileConfig> builder
    ) {
        this.definition = definition;
        this.configClass = configClass;
        this.type = type;
        this.builder = builder;
    }

    @Override
    public void serialize(T t) {
        FileConfig config = serializer().serializeFields(t, builder::build).checked();

        config.save();
        config.close();
    }

    @Override
    public T deserialize() {
        FileConfig config = builder.build();
        config.load();

        if (config.isEmpty()) {
            return createDefault();
        } else {
            return deserializer().deserializeFields(config, this::createDefault);
        }
    }

    @Override
    public T createDefault() {
        return Utils.constructUnsafely(configClass);
    }

    private ObjectSerializer serializer() {
        return ObjectSerializer.standard();
    }

    private ObjectDeserializer deserializer() {
        return ObjectDeserializer.standard();
    }
}
