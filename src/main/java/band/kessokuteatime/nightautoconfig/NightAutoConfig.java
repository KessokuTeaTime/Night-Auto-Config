package band.kessokuteatime.nightautoconfig;

import band.kessokuteatime.nightautoconfig.example.config.ExampleConfig;
import band.kessokuteatime.nightautoconfig.example.config.NightExampleConfig;
import band.kessokuteatime.nightautoconfig.config.base.ConfigType;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(NightAutoConfig.ID)
public class NightAutoConfig {
	public static final String NAME = "Night Auto Config", ID = "nightautoconfig";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    public NightAutoConfig() {
        this.onInitialize();
    }

	public void onInitialize() {
        if (!FMLLoader.isProduction()) {
            LOGGER.warn(
                    "You're running {} in a development environment. This produces extra files for testing purposes.",
                    NAME
            );

            AutoConfig.register(NightExampleConfig.class, ConfigType.DEFAULT_COMMENTED::fileWatcherSerializer);
            AutoConfig.register(ExampleConfig.class, PartitioningSerializer.wrap(ConfigType.DEFAULT_COMMENTED::fileWatcherSerializer));
        }
    }
}
