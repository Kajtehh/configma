# Configma

> [!WARNING]  
> This library is currently in an **unstable, pre-release** state.  
> It may contain bugs, breaking changes, and incomplete documentation.  
> **Use at your own risk and avoid using it in production environments.**

## Installation

### Add repository

Add GitHub Packages repository to your `pom.xml`:
```xml
<repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/Kajtehh/configma</url>
</repository>
```

### Add dependencies

Add the core library:
```xml
<dependency>
    <groupId>dev.kajteh</groupId>
    <artifactId>configma-core</artifactId>
    <version>1.0.16-SNAPSHOT</version>
</dependency>
```

Then add the loader for the format you want:

**YAML (SnakeYAML):**
```xml
<dependency>
    <groupId>dev.kajteh</groupId>
    <artifactId>configma-yaml</artifactId>
    <version>1.0.16-SNAPSHOT</version>
</dependency>
```

**JSON (GSON):**
```xml
<dependency>
    <groupId>dev.kajteh</groupId>
    <artifactId>configma-json</artifactId>
    <version>1.0.16-SNAPSHOT</version>
</dependency>
```

**XML (currently experimental):**
```xml
<dependency>
    <groupId>dev.kajteh</groupId>
    <artifactId>configma-xml</artifactId>
    <version>1.0.16-SNAPSHOT</version>
</dependency>
```