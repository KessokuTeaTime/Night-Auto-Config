package band.kessokuteatime.nightautoconfig;

import band.kessokuteatime.nightautoconfig.example.config.ExampleConfig;
import band.kessokuteatime.nightautoconfig.serializer.base.ConfigType;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class NightAutoConfig implements ClientModInitializer {
	public static final String NAME = "Night Auto Config", ID = "nightautoconfig";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitializeClient() {
		AutoConfig.register(ExampleConfig.class, ConfigType.TOML::serializer);
		ConfigHolder<ExampleConfig> holder = AutoConfig.getConfigHolder(ExampleConfig.class);
		ExampleConfig config = holder.getConfig();
    }
}
