package dev.kajteh.configma.example;

import dev.kajteh.configma.ConfigFactory;
import dev.kajteh.configma.json.JsonConfigLoader;
import dev.kajteh.configma.yaml.YamlConfigLoader;

import java.nio.file.Paths;

public class ExampleMain {

    public static void main(String[] args) {
        final var builder = ConfigFactory.builder(ExampleConfig.class);

        builder.path(Paths.get("test", "test.yml")).load(YamlConfigLoader.create());
        builder.path(Paths.get("test", "test.json")).load(JsonConfigLoader.createDefault());
    }
}
