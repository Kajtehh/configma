# Configma – Simple Bukkit Configs API
![Spigot API 1.12+](https://img.shields.io/badge/Spigot_API-1.12%2B-violet)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Repository Size](https://img.shields.io/github/repo-size/Kajtehh/bukkit-configs.svg)
[![](https://jitpack.io/v/Kajtehh/bukkit-configs.svg)](https://jitpack.io/#Kajtehh/bukkit-configs)

## Installation

### Add JitPack repository

Add this repository to your `pom.xml`:

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

### Add Configma dependencies
Add the Configma core module:
```xml
<dependency>
    <groupId>com.github.Kajtehh.bukkit-configs</groupId>
    <artifactId>configma-core</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```

If you want to use comments and descriptions in your config files, add the metadata extension (also requires Spigot API 1.19+):
```xml
<dependency>
    <groupId>com.github.Kajtehh.bukkit-configs</groupId>
    <artifactId>configma-metadata-extension</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```

## Usage Example
### Define your config class
```java
@Description(
        header = {
                "Configma",
                "Created with ❤️ by Kajteh"
        },
        footer = "👋"
)
public class ExampleConfig {

    @Comment({
            "Server kits",
            "name, permission, items 🔥"
    })
    public List<Kit> kits = List.of(
            new Kit("Default", "kit.default", List.of(new ItemStack(Material.COOKED_BEEF, 32))),
            new Kit("Vip", "kit.vip", List.of(new ItemStack(Material.GOLDEN_CARROT, 64)))
    );
}
```

### Build and load your config
```java
final Config<ExampleConfig> exampleConfig = Config.builder(this, ExampleConfig.class)
    .file("config.yml")
    .extensions(new MetadataExtension())  // optional, add this for comments and descriptions support
    .serializers(new KitSerializer())    // register custom serializers if needed
    .build();
```

### Create a serializer
```java
public class KitSerializer implements ConfigSerializer<Kit> {

    @Override
    public Class<?> getTargetType() {
        return Kit.class;
    }

    @Override
    public Object serialize(Kit kit) {
        return Map.of(
                "name", kit.name(),
                "permission", kit.permission(),
                "items", kit.items()
        );
    }

    @Override
    public Kit deserialize(Class<Kit> type, Object value) {
        final SerializedObject serializedObject = new SerializedObject(value);

        return new Kit(
                serializedObject.get("name"),
                serializedObject.get("permission"),
                serializedObject.get("items")
        );
    }
}
```
