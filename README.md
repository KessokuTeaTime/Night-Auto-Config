### <p align=right>[`→` JitPack](https://jitpack.io/#KessokuTeaTime/Night-Auto-Config)&emsp;[`→` CurseForge](https://www.curseforge.com/minecraft/mc-mods/night-auto-config)&ensp;[`→` Modrinth](https://modrinth.com/mod/night-auto-config)</p>

# Night Auto Config

Serialize everything! **Night Auto Config** is a **[Night Config](https://github.com/TheElectronWill/Night-Config)** integration for **[Auto Config (now embedded in Cloth Config).](https://modrinth.com/mod/cloth-config)**

## Implementation

**Night Auto Config** introduces a `NightConfigSerializer` to satisfy **Auto Config**'s requirement of a serializer's implementation. You can choose from all the available config formats of **Night Config,** and use the serializer just as other common serializers, even along with a `PartitioningSerializer`.

### Add to Your Project

It is recommended to use **[JitPack](https://jitpack.io/#KessokuTeaTime/Night-Auto-Config)** to implement **Night Auto Config** into your project.

<details>

<summary>Groovy</summary>

<h6 align="right">build.gradle</h6>

```groovy
repositories {
	maven { url "https://jitpack.io" }
}

dependencies {
	modImplementation "com.github.KessokuTeaTime:Night-Auto-Config:$project.nightautoconfig_version"
}
```

<h6 align="right">gradle.properties</h6>

```
nightautoconfig_version={latest}
```

</details>

<details>

<summary>Kotlin DSL</summary>

<h6 align="right">build.gradle.kts</h6>

```kotlin
repositories {
	maven { url = uri("https://jitpack.io") }
}

dependencies {
	modImplementation("com.github.KessokuTeaTime:Night-Auto-Config:$project.nightautoconfig_version")
}
```

<h6 align="right">gradle.properties</h6>

```
nightautoconfig_version={latest}
```

</details>

> [!NOTE]
> You should replace `{latest}` with the latest [`tag name`](https://github.com/KessokuTeaTime/Night-Auto-Config/tags) of **Night Auto Config.**

### Reference as Dependency

Don't forget to reference **Night Auto Config** as dependency in your mod's metadata.

<h6 align="right">fabric.mod.json / quilt.mod.json</h6>

```json
{
	"depends": {
		"nightautoconfig": "*"
	}
}
```

</details>

## Usage

The serializer implementation is at [`band.kessokuteatime.nightautoconfig.config.NightConfigSerializer`](src/main/java/band/kessokuteatime/nightautoconfig/config/NightConfigSerializer.java).

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

> [!IMPORTANT]
> Please annotate your fields with [`com.electronwill.nightconfig.core.serde.annotations.SerdeDefault`](https://github.com/TheElectronWill/night-config/blob/master/core/src/main/java/com/electronwill/nightconfig/core/serde/annotations/SerdeDefault.java) for basic compatibilities! Otherwise, serialization exceptions may happen casually.

## License

This repository is licensed under the **[GNU General Public License v3.](LICENSE)**
