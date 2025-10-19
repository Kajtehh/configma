# Configma

> [!WARNING]  
> This library is currently in an **unstable, pre-release** state.  
> It may contain bugs, breaking changes, and incomplete documentation.  
> **Use at your own risk and avoid using it in production environments.**

## 1. Installation

### Add repository

Add GitHub Packages repository to your `pom.xml`:
```xml
<repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/Kajtehh/configma</url>
</repository>
```

### Add dependencies

Add the core library
```xml
<dependency>
    <groupId>dev.kajteh</groupId>
    <artifactId>configma-core</artifactId>
    <version>1.0.7-SNAPSHOT</version>
</dependency>
```

Then add the parser for the format you want:

**YAML**:
```xml
<dependency>
    <groupId>dev.kajteh</groupId>
    <artifactId>configma-yaml</artifactId>
    <version>1.0.7-SNAPSHOT</version>
</dependency>
```

**JSON:**
```xml
<dependency>
    <groupId>dev.kajteh</groupId>
    <artifactId>configma-json</artifactId>
    <version>1.0.7-SNAPSHOT</version>
</dependency>
```

## 2. Define Your Configuration
Define a configuration class (`AppConfig`). The nested models like `User` can be a class or a record — the details are up to you.

### Create a configuration object
```java
public class AppConfig {

    // 'User' is a nested object (class or record)
    public User user = new User(
            UUID.randomUUID(),
            "Kajteh",
            Map.of(
                    "level", 123,
                    "theme", "dark",
                    "notifications", true
            )
    );

    public boolean test = true;
}
```

### 3. Loading configuration
```java
final var config = ConfigFactory.builder(AppConfig.class)
        .file(new File("config.yml"))
        .parser(new YamlConfigParser()) // or JsonConfigParser
        .serializer(new UserSerializer()) // optional, only for custom serializers
        .initialize();
```
Access configuration values:
```java
final var user = config.get().user;

// or use lambda
config.get(appConfig -> { 
    final var testValue = appConfig.test;
});
```

### Editing and Saving Configuration
```java
config.edit(appConfig -> {
    appConfig.user = new User(UUID.randomUUID(), "Another User", Map.of("level", 22));
    appConfig.test = false;
});

config.save();   // persist current config to file
config.reload(); // load values from file into memory
```

### Creating a custom Serializer
**Object Serializer** – used for serializing/deserializing complex objects like User. Handles mapping between fields in your object and the file format:
```java
public class UserSerializer implements ObjectSerializer<User> {

    @Override
    public void serialize(SerializationContext context, User user) {
        context.set("uuid", user.uuid);
        context.set("name", user.name);
        context.set("properties", user.properties);
    }

    @Override
    public User deserialize(DeserializationContext context) {
        return new User(
                context.get("uuid", UUID.class),
                context.get("name", String.class),
                context.get("properties", new TypeReference<Map<String, Object>>() {}.getType())
        );
    }

    @Override
    public boolean matches(Class<?> type) {
        return User.class.isAssignableFrom(type);
    }
}
```
**Value Serializer** – used for simple/primitive types or logical mappings. Example: boolean values represented as `"yes"` / `"no"` in the file:
```java
public class YesNoBooleanSerializer implements ValueSerializer<Boolean> {

    @Override
    public Object serialize(final Boolean value) {
        return value ? "yes" : "no"; // true -> "yes", false -> "no"
    }

    @Override
    public Boolean deserialize(final Object raw) {
        final String string = raw.toString().toLowerCase();
        
        return switch (string) {
            case "yes", "true" -> true;
            case "no", "false" -> false;
            default -> throw new IllegalArgumentException("Cannot convert to Boolean: " + raw);
        };
    }

    @Override
    public boolean matches(final Class<?> type) {
        return Boolean.class.isAssignableFrom(type) || type == boolean.class;
    }
}
```

### Example configuration output
**YAML**
```yml
user:
  uuid: "5f5a65bd-9b2e-49e6-a6a0-6cbcd49d2ace"
  name: "Kajteh"
  properties:
    level: 123
    theme: "dark"
    notifications: true
test: "yes"
```
- `user` – nested object serialized with `UserSerializer`.
- `test` – boolean serialized with `YesNoBooleanSerializer` (`true` → `yes`).

**JSON**
```json
{
  "user": {
    "uuid": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Kajteh",
    "properties": {
      "level": 123,
      "theme": "dark",
      "notifications": true
    }
  },
  "test": "yes"
}
```
- JSON mirrors the same structure as YAML.
- Nested objects and logical value serializers are applied automatically.