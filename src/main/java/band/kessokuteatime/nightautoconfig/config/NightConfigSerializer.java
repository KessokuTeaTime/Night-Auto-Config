package band.kessokuteatime.nightautoconfig.config;

import band.kessokuteatime.nightautoconfig.NightAutoConfig;
import band.kessokuteatime.nightautoconfig.config.base.ConfigType;
import band.kessokuteatime.nightautoconfig.serde.NightDeserializers;
import band.kessokuteatime.nightautoconfig.serde.NightSerializers;
import band.kessokuteatime.nightautoconfig.util.AnnotationUtil;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.GenericBuilder;
import com.electronwill.nightconfig.core.serde.ObjectDeserializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;
import com.electronwill.nightconfig.core.serde.annotations.SerdeDefault;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public void serialize(@NotNull T t) throws SerializationException {
        Path path = type.getConfigPath(definition);
        try {
            createFile(path);
        } catch (IOException e) {
            throw new SerializationException(e);
        }

        try {
            FileConfig config = serializer(configClass).serializeFields(t, builder::build).checked();

            config.save();
            config.close();
        } catch (Exception e) {
            NightAutoConfig.LOGGER.error(
                    "Serialization failed for config {}! This will cause the program to stop writing anything to the file.",
                    type.getRelativeConfigPath(definition)
            );
            NightAutoConfig.LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public T deserialize() throws SerializationException {
        Path path = type.getConfigPath(definition);
        try {
            createFile(path);
        } catch (IOException e) {
            throw new SerializationException(e);
        }

        try {
            FileConfig config = builder.build();
            config.load();

            if (config.isEmpty()) {
                return createDefault();
            } else {
                return deserializer(configClass).deserializeFields(config, this::createDefault);
            }
        } catch (Exception e) {
            NightAutoConfig.LOGGER.error(
                    "Deserialization failed for config {}! This will cause the program to ignore the existing modifications and use default values. This might be caused by a missing of {} on restricting value fallbacks.",
                    type.getRelativeConfigPath(definition),
                    SerdeDefault.class
            );
            NightAutoConfig.LOGGER.error(e.getMessage(), e);
            return createDefault();
        }
    }

    @Override
    public T createDefault() {
        return Utils.constructUnsafely(configClass);
    }

    private void createFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }
    }

    private ObjectSerializer serializer(Class<?> cls) {
        var builder = ObjectSerializer.builder();
        NightSerializers.provideToBuilder(builder);

        // Register serializer providers
        var providers = AnnotationUtil.getSerializerProviders(cls);
        providers.forEach(builder::withSerializerProvider);

        return builder.build();
    }

    private ObjectDeserializer deserializer(Class<?> cls) {
        var builder = ObjectDeserializer.builder();
        NightDeserializers.provideToBuilder(builder);

        // Register deserializer providers
        var providers = AnnotationUtil.getDeserializerProviders(cls);
        providers.forEach(builder::withDeserializerProvider);

        return builder.build();
    }
}
