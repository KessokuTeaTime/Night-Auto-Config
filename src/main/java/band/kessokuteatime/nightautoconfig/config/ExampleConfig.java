package band.kessokuteatime.nightautoconfig.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "example")
public class ExampleConfig implements ConfigData {
    public int exampleInt = 10;
    public double exampleDouble = 3.14159;
    public float exampleFloat = 2.71828F;
    public boolean exampleBoolean = true;
    public String exampleString = "Hello, World!";
}
