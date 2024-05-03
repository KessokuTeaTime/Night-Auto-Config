package band.kessokuteatime.nightautoconfig.example.config;

import band.kessokuteatime.nightautoconfig.annotation.Nested;
import band.kessokuteatime.nightautoconfig.annotation.SpecInList;
import band.kessokuteatime.nightautoconfig.annotation.SpecInRangeDouble;
import band.kessokuteatime.nightautoconfig.annotation.SpecOfClass;
import band.kessokuteatime.nightautoconfig.converter.FloatToDoubleConverter;
import band.kessokuteatime.nightautoconfig.spec.InListProvider;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.SpecEnum;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.*;

@Config(name = "example")
public class ExampleConfig implements ConfigData {
    public enum ExampleEnum {
        FIRST,
        SECOND,
        THIRD
    }

    public static class ExampleStringInListProvider implements InListProvider<String> {
        @Override
        public Collection<String> acceptableValues() {
            return List.of("case 1", "case 2", "case 3");
        }
    }

    public static class ExampleEnumInListProvider implements InListProvider<ExampleEnum> {
        @Override
        public Collection<ExampleEnum> acceptableValues() {
            return List.of(ExampleEnum.FIRST, ExampleEnum.SECOND);
        }
    }

    public int exampleInt = 10;

    public double exampleDouble = 3.14159;

    @Conversion(FloatToDoubleConverter.Impl.class)
    @SpecInRangeDouble(max = 5)
    public float exampleFloat = 2.71828F;

    public boolean exampleBoolean = true;

    public String exampleString = "Hello, World!";

    @Path("stringWithCustomKey")
    public String exampleString2 = "Another String.";

    //@ConfigEntry.Category("category")
    public String categorizedString = "Categorized!";

    @ConfigEntry.Gui.TransitiveObject
    //@ConfigEntry.Category("inner")
    public InnerConfig innerConfig = new InnerConfig();

    public ArrayList<String> exampleStringArrayList = new ArrayList<>(List.of(
            "one",
            "two",
            "three"
    ));

    //@Conversion(MapToConfigConverter.class)
    public Map<String, Integer> exampleStringIntHashMap = new HashMap<>(Map.of(
            "one", 1,
            "two", 2,
            "three", 3
    ));

    @Nested
    public static class InnerConfig {
        public int innerInt = 42;

        public String innerString = "S.T.A.Y.";

        @SpecInList(definition = ExampleStringInListProvider.class)
        public String restrictedString = "case 1";

        @SpecOfClass(ExampleEnum.class)
        public ExampleEnum innerEnum = ExampleEnum.SECOND;

        @SpecEnum(method = EnumGetMethod.ORDINAL_OR_NAME)
        @SpecInList(definition = ExampleEnumInListProvider.class)
        public ExampleEnum restrictedEnum = ExampleEnum.SECOND;
    }
}
