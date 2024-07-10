package band.kessokuteatime.nightautoconfig.example.config;

import com.electronwill.nightconfig.core.serde.annotations.SerdeDefault;
import com.electronwill.nightconfig.core.serde.annotations.SerdeKey;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.*;

@Config(name = "example")
public class NightExampleConfig implements ConfigData {
    public enum ExampleEnum {
        FIRST,
        SECOND,
        THIRD,
        FOURTH,
        FIFTH,
        SIXTH,
        SEVENTH,
        EIGHTH,
        NINTH,
        TENTH
    }
    
    public int exampleInt = 10;

    public long exampleLong = 100L;

    public double exampleDouble = 3.14159;

    public float exampleFloat = 2.71828F;

    public boolean exampleBoolean = true;

    public String exampleString = "Hello, World!";

    @SerdeKey("stringWithCustomKey")
    public String exampleString2 = "Another String.";

    //@ConfigEntry.Category("category")
    public String categorizedString = "Categorized!";

    public ArrayList<String> exampleStringArrayList = new ArrayList<>(List.of(
            "one",
            "two",
            "three"
    ));

    /*
    public Map<String, Integer> exampleStringIntMap = new LinkedHashMap<>(Map.of(
            "one", 1,
            "two", 2,
            "three", 3
    ));
     */

    @ConfigEntry.Gui.TransitiveObject
    //@ConfigEntry.Category("inner")
    public InnerConfig innerConfig = new InnerConfig();

    public static class InnerConfig {
        public int innerInt = 42;

        public String innerString = "S.T.A.Y.";

        public String restrictedString = "case 1";

        public ExampleEnum innerEnum = ExampleEnum.SECOND;

        public ExampleEnum restrictedEnum = ExampleEnum.SECOND;
    }
}
