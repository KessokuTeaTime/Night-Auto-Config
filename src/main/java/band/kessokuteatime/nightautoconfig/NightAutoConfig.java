package band.kessokuteatime.nightautoconfig;

import band.kessokuteatime.nightautoconfig.example.config.ExampleConfig;
import band.kessokuteatime.nightautoconfig.example.config.NightExampleConfig;
import band.kessokuteatime.nightautoconfig.config.base.ConfigType;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NightAutoConfig implements ModInitializer {
	public static final String NAME = "Night Auto Config", ID = "nightautoconfig";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    @Override
	public void onInitialize() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            var location = getClass().getProtectionDomain().getCodeSource().getLocation();
            boolean jarred = location.toString().startsWith("jar:");

            if (!jarred) {
                LOGGER.warn(
                        "You're running {} in a development environment. This produces extra files for testing purposes.",
                        NAME
                );

                AutoConfig.register(NightExampleConfig.class, ConfigType.DEFAULT_COMMENTED::fileWatcherSerializer);
                AutoConfig.register(ExampleConfig.class, PartitioningSerializer.wrap(ConfigType.DEFAULT_COMMENTED::fileWatcherSerializer));
            }
        }
    }
}
