package band.kessokuteatime.nightautoconfig.example.config;

import com.electronwill.nightconfig.core.serde.annotations.SerdeComment;
import com.electronwill.nightconfig.core.serde.annotations.SerdeDefault;
import com.electronwill.nightconfig.core.serde.annotations.SerdeKey;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

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

    private transient final Supplier<Integer> exampleIntProvider = () -> 10;

    @SerdeDefault(provider = "exampleIntProvider")
    public int exampleInt = exampleIntProvider.get();

    private transient final Supplier<Long> exampleLongProvider = () -> 100L;

    @SerdeComment("This is a comment.")
    @SerdeComment("This is another comment.")
    @SerdeDefault(provider = "exampleLongProvider")
    public long exampleLong = exampleLongProvider.get();

    private transient final Supplier<Double> exampleDoubleProvider = () -> 3.14159;

    @SerdeDefault(provider = "exampleDoubleProvider")
    public double exampleDouble = exampleDoubleProvider.get();

    private transient final Supplier<Float> exampleFloatProvider = () -> 2.71828F;

    @SerdeDefault(provider = "exampleFloatProvider")
    public float exampleFloat = exampleFloatProvider.get();

    private transient final Supplier<Boolean> exampleBooleanProvider = () -> true;

    @SerdeDefault(provider = "exampleBooleanProvider")
    public boolean exampleBoolean = exampleBooleanProvider.get();

    private transient final Supplier<Color> exampleColorProvider = () -> Color.ORANGE;

    @SerdeDefault(provider = "exampleColorProvider")
    public Color exampleColor = exampleColorProvider.get();

    private transient final Supplier<String> exampleStringProvider = () -> "Hello, world!";

    @SerdeDefault(provider = "exampleStringProvider")
    public String exampleString = exampleStringProvider.get();

    private transient final Supplier<String> exampleString2Provider = () -> "Another string.";

    @SerdeDefault(provider = "exampleString2Provider")
    @SerdeKey("stringWithCustomKey")
    public String exampleString2 = exampleString2Provider.get();

    private transient final Supplier<String> categorizedStringProvider = () -> "Categorized!";

    @SerdeDefault(provider = "categorizedStringProvider")
    @ConfigEntry.Category("category")
    public String categorizedString = categorizedStringProvider.get();

    private transient final Supplier<List<String>> exampleStringListProvider = () ->List.of(
            "one",
            "two",
            "three"
    );

    @SerdeDefault(provider = "exampleStringListProvider")
    public List<String> exampleStringList = exampleStringListProvider.get();

    private transient final Supplier<Map<String, Integer>> exampleStringIntMapProvider = () -> new LinkedHashMap<>(Map.of(
            "one", 1,
            "two", 2,
            "three", 3
    ));

    @SerdeDefault(provider = "exampleStringIntMapProvider")
    public Map<String, Integer> exampleStringIntMap = exampleStringIntMapProvider.get();

    private transient final Supplier<InnerConfig> innerConfigSupplier = InnerConfig::new;

    @SerdeComment("This is a comment too.")
    @SerdeDefault(provider = "innerConfigSupplier")
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("inner")
    public InnerConfig innerConfig = innerConfigSupplier.get();

    public static class InnerConfig {
        private transient final Supplier<Integer> innerIntProvider = () -> 42;

        @SerdeDefault(provider = "innerIntProvider")
        public int innerInt = innerIntProvider.get();

        private transient final Supplier<String> innerStringProvider = () -> "S.T.A.Y";

        @SerdeDefault(provider = "innerStringProvider")
        public String innerString = innerStringProvider.get();

        private transient final Supplier<ExampleEnum> innerEnumProvider = () -> ExampleEnum.SECOND;

        @SerdeDefault(provider = "innerEnumProvider")
        public ExampleEnum innerEnum = innerEnumProvider.get();
    }
}
