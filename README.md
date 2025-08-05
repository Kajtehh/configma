# Configma • Simple Bukkit Configs API
![Spigot API 1.12+](https://img.shields.io/badge/Spigot_API-1.12%2B-violet)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Repository Size](https://img.shields.io/github/repo-size/Kajtehh/configma.svg)
[![](https://jitpack.io/v/Kajtehh/configma.svg)](https://jitpack.io/#Kajtehh/configma)

Configma is a lightweight configuration library for Bukkit, Spigot, and Paper. It allows effortless serialization and deserialization of Java objects to YAML — directly from your objects, with no need to manually create or write YAML files.

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
    <groupId>com.github.Kajtehh.configma</groupId>
    <artifactId>configma-core</artifactId>
    <version>1.0.2-SNAPSHOT</version>
</dependency>
```

If you want to use `comments` and `descriptions` in your config files, add the metadata extension (also requires Spigot API 1.19+):
```xml
<dependency>
    <groupId>com.github.Kajtehh.configma</groupId>
    <artifactId>configma-metadata-extension</artifactId>
    <version>1.0.2-SNAPSHOT</version>
</dependency>
```

## Usage Example
### 📝 Define your config class
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

    @Comment("Enums? No worries, it just works like any other type!")
    public Language language = Language.PL;

    // transient, final, and @IgnoreField fields are fully excluded—never saved or loaded.
    @IgnoreField
    public String ignoredField = "This won't be saved";

    // @Pathname customizes the YAML path for config subsections
    @Pathname("messages")
    public MessagesSection messagesSection = new MessagesSection();

    // Classes implementing ConfigSection define sections of the config
    public static class MessagesSection implements ConfigSection {
        public String hello = "Hello World!";
    }
}
```

### 🚀 Build and load your config
```java
final Config<ExampleConfig> exampleConfig = Config.builder(this, ExampleConfig.class)
    .file("config.yml") // or provide a File instance for a custom file location
    .extensions(new MetadataExtension())  // optional, add this for comments and descriptions support
    .serializers(new KitSerializer())    // register custom serializers if needed
    .build();
```

### 🔍 Accessing configuration values
Once you've built your config, accessing values is simple and flexible:
```java
exampleConfig.get(config -> {
    getLogger().info("Language: " + config.language.name());
});
```
```java
// Get a specific value using a lambda
Language language = exampleConfig.get(config -> config.language);
```
```java
// Or access the config directly
Language language = exampleConfig.get().language;
```

### ✏️ Editing, Saving & Reloading the config
```java
// Edit the config (does NOT save to file unless specified)
// Using this method is recommended to ensure you work with the latest data when editing.
exampleConfig.edit(config -> config.language = Language.EN);

// Save current config state to file
exampleConfig.save();
```
```java
// Reload config from file (useful if edited externally)
exampleConfig.reload();
```

### 🧩 Creating a serializer
Custom serializers let you control how your objects are converted to and from YAML. They don't have to return just a `Map` — you can serialize to any object type that fits your needs.
```java
public class KitSerializer implements ConfigSerializer<Kit> {

    @Override
    public Class<?> getTargetType() {
        return Kit.class;
    }

    @Override
    public Object serialize(Kit kit) {
        // You can serialize your object to any structure,
        // here we use a Map, but it could be any other object type
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
