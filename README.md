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

Add the core library
```xml
<dependency>
    <groupId>dev.kajteh</groupId>
    <artifactId>configma-core</artifactId>
    <version>1.0.6-SNAPSHOT</version>
</dependency>
```

Then add the adapter you want to use:

**Using YAML**:
```xml
<dependency>
    <groupId>dev.kajteh</groupId>
    <artifactId>configma-yaml</artifactId>
    <version>1.0.6-SNAPSHOT</version>
</dependency>
```

**Using JSON:**
```xml
<dependency>
    <groupId>dev.kajteh</groupId>
    <artifactId>configma-json</artifactId>
    <version>1.0.6-SNAPSHOT</version>
</dependency>
```