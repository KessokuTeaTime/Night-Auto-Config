package band.kessokuteatime.nightautoconfig;

import band.kessokuteatime.nightautoconfig.config.ExampleConfig;
import band.kessokuteatime.nightautoconfig.serializer.ConfigType;
import band.kessokuteatime.nightautoconfig.serializer.NightConfigSerializer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NightAutoConfig implements ClientModInitializer {
	public static final String NAME = "Night Auto Config", ID = "nightautoconfig";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitializeClient() {
		AutoConfig.register(ExampleConfig.class, ConfigType.JSON::serializer);
		ConfigHolder<ExampleConfig> holder = AutoConfig.getConfigHolder(ExampleConfig.class);
		ExampleConfig config = holder.getConfig();
		LOGGER.info("Example string: " + config.exampleString);
	}
}
