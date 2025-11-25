package dev.kajteh.configma.example;

import dev.kajteh.configma.ConfigFactory;
import dev.kajteh.configma.yaml.YamlConfigParser;

import java.nio.file.Paths;

public class ExampleMain {

    public static void main(String[] args) {
        ConfigFactory.builder(ExampleConfig.class)
                .format(YamlConfigParser.standard()
                        .withFormatter(name -> name.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toUpperCase()))
                .file(Paths.get("test", "test.yml"))
                .build();

    }
}
