package dev.kajteh.configma.example;

import dev.kajteh.configma.ConfigFactory;
import dev.kajteh.configma.yaml.YamlConfigParser;

import java.nio.file.Paths;

public class ExampleMain {

    public static void main(String[] args) {
        var config = ConfigFactory.builder(ExampleConfig.class)
                .format(YamlConfigParser.createDefault())
                .file(Paths.get("test", "config.yml"))
                .build();

        System.out.println(config.get().database().host());
    }
}
