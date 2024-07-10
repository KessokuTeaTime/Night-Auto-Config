### <p align=right>[`→` CurseForge](https://www.curseforge.com/minecraft/mc-mods/1061154)&ensp;[`→` Modrinth](https://modrinth.com/mod/night-auto-config)</p>

# Night Auto Config

Serialize everything! **Night Auto Config** is a **[Night Config](https://github.com/TheElectronWill/Night-Config)** integration for **[Auto Config (now embedded in Cloth Config).](https://modrinth.com/mod/cloth-config)**

**Night Auto Config** introduces a `NightConfigSerializer` to satisfy **Auto Config**'s requirement of a serializer's implementation. You can choose from all the available config formats of **Night Config,** and use the serializer just as other common serializers, even along with a `PartitioningSerializer`.

Here's a brief example:

`MyConfig.java`
```java
// Use annotations provided by Night Auto Config to define serializer providers and deserializer providers at runtime!
@SerializerProvider(MyClassSerializerProvider.class)
@Config(name = "my_config")
public class MyConfig implements ConfigData {
    // Something...
    
    private transient final Supplier<String> someStringProvider = () -> "default";
    
    // All Night Config annotations are available
    @SerdeDefault(provider = "someStringProvider")
    public String someString = someStringProvider.get();
    
    // Night Auto Config provides some interfaces for convenience implementations
    // For example, `UnifiedSerializerProvider<T, R>` satisfies both `ValueSerializer<T, R>` and `ValueSerializerProvider<T, R>`
    public static class MyClassSerializerProvider implements UnifiedSerializerProvider<MyClass, String> {
        // ...
    }
    
    // A custom serializer provider for `MyClass` is already specified at type definition
    public MyClass someInstance = new MyClass();
}
```

`MyMod.java`
```java
public class MyMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Don't remember to register into Auto Config at initialize
        AutoConfig.register(NightExampleConfig.class, ConfigType.DEFAULT_COMMENTED::fileWatcherSerializer);
    }
}
```

For runtime examples, checkout [this package.](/src/main/java/band/kessokuteatime/nightautoconfig/example/config)

## License

This repository is licensed under the **[GNU General Public License v3.](LICENSE)**
