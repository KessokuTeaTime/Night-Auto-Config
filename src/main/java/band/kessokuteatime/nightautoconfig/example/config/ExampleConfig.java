package band.kessokuteatime.nightautoconfig.example.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "example")
public class ExampleConfig implements ConfigData {
    public int exampleInt = 10;

    public double exampleDouble = 3.14159;

    //@Conversion(FloatToDoubleConverter.class)
    public float exampleFloat = 2.71828F;

    public boolean exampleBoolean = true;

    public String exampleString = "Hello, World!";

    //@Path("stringWithCustomKey")
    public String exampleString2 = "Another String.";

    @ConfigEntry.Category("category")
    public String categorizedString = "Categorized!";

    /*
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("inner")
    public InnerConfig innerConfig = new InnerConfig();
    
     */

    public static class InnerConfig {
        public int innerInt = 42;

        public String innerString = "S.T.A.Y.";
    }
}
