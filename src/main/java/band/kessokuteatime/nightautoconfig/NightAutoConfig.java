package band.kessokuteatime.nightautoconfig;

import band.kessokuteatime.nightautoconfig.example.config.ExampleConfig;
import band.kessokuteatime.nightautoconfig.serializer.ConfigType;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
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

	public static <T> T unsafeBaseModule(T t) {
		try {
			Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
			Field field = unsafeClass.getDeclaredField("theUnsafe");

			field.setAccessible(true);
			Unsafe unsafe = (Unsafe) field.get(null);

			Module baseModule = Object.class.getModule();
			long addr = unsafe.objectFieldOffset(Class.class.getDeclaredField("module"));
			unsafe.getAndSetObject(t, addr, baseModule);
			return t;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
