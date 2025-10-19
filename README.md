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
    <version>1.0.8-SNAPSHOT</version>
</dependency>
```

Then add the parser for the format you want:

**YAML**:
```xml
<dependency>
    <groupId>dev.kajteh</groupId>
    <artifactId>configma-yaml</artifactId>
    <version>1.0.8-SNAPSHOT</version>
</dependency>
```

**JSON:**
```xml
<dependency>
    <groupId>dev.kajteh</groupId>
    <artifactId>configma-json</artifactId>
    <version>1.0.8-SNAPSHOT</version>
</dependency>
```

## 2. Define Your Configuration
Define a configuration class (`AppConfig`).

### Create a configuration object
```java
public record Task(String description, boolean completed) {}
```
```java
public record User(UUID id, String name, List<Task> tasks) {}
```
```java
public class AppConfig {
    
    public List<User> users = List.of(
            new User(
                    UUID.randomUUID(),
                    "Kajteh",
                    List.of(
                            new Task("Code Configma", true),
                            new Task("Finish docs", false),
                            new Task("Learn for maths", false),
                            new Task("Go to sleep", false)
                    )
            ),
            new User(
                    UUID.randomUUID(),
                    "Another User",
                    List.of(
                            new Task("Complete tutorial", true)
                    )
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
        .serializer(new TaskSerializer(), new UserSerializer()) // optional, only for custom serializers
        .initialize();
```
Access configuration values:
```java
final var users = config.get().users;
users.forEach(user -> System.out.println(user.name()));

// or use lambda
config.get(cfg -> System.out.println("Test: " + cfg.test));
```

### Editing and Saving Configuration
```java
config.edit(cfg -> cfg.test = false);

config.save();   // persist current config to file
config.reload(); // load values from file into memory
```

### Creating a custom Serializer
**Object Serializer** – used for serializing/deserializing complex objects like User. Handles mapping between fields in your object and the file format:
```java
public class TaskSerializer implements ObjectSerializer<Task> {

    @Override
    public void serialize(SerializationContext context, Task task) {
        context.set("description", task.description());
        context.set("completed", task.completed());
    }

    @Override
    public Task deserialize(DeserializationContext context) {
        return new Task(
                context.get("description", String.class),
                context.get("completed", Boolean.class)
        );
    }

    @Override
    public boolean matches(Class<?> type) {
        return Task.class.isAssignableFrom(type);
    }
}
```
```java
public class UserSerializer implements ObjectSerializer<User> {

    @Override
    public void serialize(SerializationContext context, User user) {
        context.set("id", user.id());
        context.set("name", user.name());
        context.set("tasks", user.tasks(), new TypeReference<List<Task>>() {}.getType()); // Needed to tell the serializer the element type due to Java's type erasure
    }

    @Override
    public User deserialize(DeserializationContext context) {
        return new User(
                context.get("id", UUID.class),
                context.get("name", String.class),
                context.get("tasks", new TypeReference<List<Task>>() {}.getType()) // Needed so deserializer knows it's a List of Task, not a raw List
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
    public Object serialize(Boolean value) {
        return value ? "yes" : "no"; // true -> "yes", false -> "no"
    }

    @Override
    public Boolean deserialize(Object raw) {
        final String string = raw.toString().toLowerCase();
        
        return switch (string) {
            case "yes", "true" -> true;
            case "no", "false" -> false;
            default -> throw new IllegalArgumentException("Cannot convert to Boolean: " + raw);
        };
    }

    @Override
    public boolean matches(Class<?> type) {
        return Boolean.class.isAssignableFrom(type) || type == boolean.class;
    }
}
```

### Example configuration output
**YAML**
```yml
users:
  - id: "cf59356d-5860-4757-898c-48b6a0425794"
    name: "Kajteh"
    tasks:
      - description: "Code Configma"
        completed: true
      - description: "Finish docs"
        completed: false
      - description: "Learn for maths"
        completed: false
      - description: "Go to sleep"
        completed: false
  - id: "961fa571-3d0f-41b5-8958-397e93adbc0b"
    name: "Another User"
    tasks:
      - description: "Complete tutorial"
        completed: true
```

**JSON**
```json
{
  "users": [
    {
      "id": "cf59356d-5860-4757-898c-48b6a0425794",
      "name": "Kajteh",
      "tasks": [
        { "description": "Code Configma", "completed": true },
        { "description": "Finish docs", "completed": false },
        { "description": "Learn for maths", "completed": false },
        { "description": "Go to sleep", "completed": false }
      ]
    },
    {
      "id": "961fa571-3d0f-41b5-8958-397e93adbc0b",
      "name": "Another User",
      "tasks": [
        { "description": "Complete tutorial", "completed": true }
      ]
    }
  ]
}
```