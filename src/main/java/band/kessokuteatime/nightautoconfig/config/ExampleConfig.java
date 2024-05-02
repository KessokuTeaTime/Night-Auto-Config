package band.kessokuteatime.nightautoconfig.config;

import band.kessokuteatime.nightautoconfig.converter.FloatToDoubleConverter;
import com.electronwill.nightconfig.core.conversion.Conversion;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "example")
public class ExampleConfig implements ConfigData {
    public int exampleInt = 10;

    public double exampleDouble = 3.14159;

    @Conversion(FloatToDoubleConverter.class)
    public float exampleFloat = 2.71828F;

    public boolean exampleBoolean = true;

    public String exampleString = "Hello, World!";
}
