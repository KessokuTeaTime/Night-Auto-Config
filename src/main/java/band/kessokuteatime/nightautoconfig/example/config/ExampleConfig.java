package band.kessokuteatime.nightautoconfig.example.config;

import band.kessokuteatime.nightautoconfig.annotation.Nested;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

import java.util.Arrays;
import java.util.List;

@Config(name = "autoconfig1u_example")
/*
@Config.Gui.Background("minecraft:textures/block/oak_planks.png")
@Config.Gui.CategoryBackground(
        category = "b",
        background = "minecraft:textures/block/stone.png"
)

 */
public class ExampleConfig extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("a")
    @ConfigEntry.Gui.TransitiveObject
    public ModuleA moduleA = new ModuleA();

    @ConfigEntry.Category("a")
    @ConfigEntry.Gui.TransitiveObject
    public ModuleEmpty moduleEmpty = new ModuleEmpty();

    @ConfigEntry.Category("b")
    @ConfigEntry.Gui.TransitiveObject
    public ModuleB moduleB = new ModuleB();

    @Config(name = "module_a")
    @Nested
    public static class ModuleA implements ConfigData {
        @ConfigEntry.Gui.PrefixText
        public boolean aBoolean = true;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public ExampleEnum anEnum;

        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ExampleEnum anEnumWithButton;

        public String aString;

        @ConfigEntry.Gui.CollapsibleObject(
                startExpanded = true
        )
        public ExamplePairOfIntPairs anObject;

        public List<Integer> list;

        public int[] array;

        public List<ExamplePairOfInts> complexList;

        public ExamplePairOfInts[] complexArray;

        public ModuleA() {
            this.anEnum = ExampleEnum.FOO;
            this.anEnumWithButton = ExampleEnum.FOO;
            this.aString = "hello";
            this.anObject = new ExamplePairOfIntPairs(new ExamplePairOfInts(), new ExamplePairOfInts(3, 4));
            this.list = Arrays.asList(1, 2, 3);
            this.array = new int[]{1, 2, 3};
            this.complexList = Arrays.asList(new ExamplePairOfInts(0, 1), new ExamplePairOfInts(3, 7));
            this.complexArray = new ExamplePairOfInts[]{new ExamplePairOfInts(0, 1), new ExamplePairOfInts(3, 7)};
        }
    }

    @Config(name = "empty")
    @Nested
    public static class ModuleEmpty implements ConfigData {
    }

    @Nested
    @Config(name = "module_b")
    public static class ModuleB implements ConfigData {
        @ConfigEntry.BoundedDiscrete(min = -1000L, max = 2000L)
        public int intSlider = 500;

        @ConfigEntry.BoundedDiscrete(min = -1000L, max = 2000L)
        public Long longSlider = 500L;

        @ConfigEntry.Gui.TransitiveObject
        public ExamplePairOfIntPairs anObject = new ExamplePairOfIntPairs(new ExamplePairOfInts(), new ExamplePairOfInts(3, 4));

        @ConfigEntry.Gui.Excluded
        public List<ExamplePairOfInts> aList = Arrays.asList(new ExamplePairOfInts(), new ExamplePairOfInts(3, 4));

        @ConfigEntry.ColorPicker
        public int color = 16777215;
    }

    public static class ExamplePairOfIntPairs {
        @ConfigEntry.Gui.CollapsibleObject
        public ExamplePairOfInts first;

        @ConfigEntry.Gui.CollapsibleObject
        public ExamplePairOfInts second;

        public ExamplePairOfIntPairs() {
            this(new ExamplePairOfInts(), new ExamplePairOfInts());
        }

        public ExamplePairOfIntPairs(ExamplePairOfInts first, ExamplePairOfInts second) {
            this.first = first;
            this.second = second;
        }
    }

    public static class ExamplePairOfInts {
        public int foo;
        public int bar;

        public ExamplePairOfInts() {
            this(1, 2);
        }

        public ExamplePairOfInts(int foo, int bar) {
            this.foo = foo;
            this.bar = bar;
        }
    }

    public enum ExampleEnum {
        FOO, BAR, BAZ
    }
}
