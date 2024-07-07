package band.kessokuteatime.nightautoconfig;

import band.kessokuteatime.nightautoconfig.example.config.ExampleConfig;
import band.kessokuteatime.nightautoconfig.example.config.NightExampleConfig;
import band.kessokuteatime.nightautoconfig.serializer.base.ConfigType;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NightAutoConfig implements ClientModInitializer {
	public static final String NAME = "Night Auto Config", ID = "nightautoconfig";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    @Override
	public void onInitializeClient() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            LOGGER.warn(
                    "You're running {} in a development environment. This produces extra files for testing purposes.",
                    NAME
            );

            AutoConfig.register(NightExampleConfig.class, ConfigType.JSON::defaultSerializer);
            AutoConfig.register(ExampleConfig.class, PartitioningSerializer.wrap(ConfigType.JSON::defaultSerializer));
        }
    }
}
