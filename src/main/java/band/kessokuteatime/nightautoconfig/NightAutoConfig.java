package band.kessokuteatime.nightautoconfig;

import band.kessokuteatime.nightautoconfig.example.config.ExampleConfig;
import band.kessokuteatime.nightautoconfig.serializer.base.ConfigType;
import com.electronwill.nightconfig.core.Config;
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
