package band.kessokuteatime.nightautoconfig;

import band.kessokuteatime.nightautoconfig.conversion.api.FloatToDoubleConverter;
import band.kessokuteatime.nightautoconfig.conversion.api.NumberToLongConverter;
import band.kessokuteatime.nightautoconfig.example.config.ExampleConfig;
import band.kessokuteatime.nightautoconfig.example.config.NightExampleConfig;
import band.kessokuteatime.nightautoconfig.serializer.base.ConfigType;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Converter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class NightAutoConfig implements ClientModInitializer {
	public static final String NAME = "Night Auto Config", ID = "nightautoconfig";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    public static final Map<Predicate<Class<?>>, Class<? extends Converter<?, ?>>> DEFAULT_CONVERTERS = Map.of(
            c -> List.of(Float.class, float.class).contains(c), FloatToDoubleConverter.Impl.class
    );

    @Override
	public void onInitializeClient() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            LOGGER.warn(
                    "You're running {} in a development environment. This produces extra files for testing purposes.",
                    NAME
            );

            AutoConfig.register(NightExampleConfig.class, ConfigType.TOML::defaultSerializer);
            AutoConfig.register(ExampleConfig.class, PartitioningSerializer.wrap(ConfigType.TOML::defaultSerializer));
        }
    }

    public static <C extends Config> void normalize(C config) {
        // Do not normalize the top layer, but recursively normalize the inner layers
        config.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Config)
                .forEach(entry -> {
                    Config innerConfig = entry.getValue();
                    Config normalizedInnerConfig = ConfigType.DEFAULT_SIMPLE.wrap(innerConfig);
                    normalize(normalizedInnerConfig);

                    config.set(entry.getKey(), normalizedInnerConfig);
                });
    }
}
