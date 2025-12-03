package dev.kajteh.configma.example;

import dev.kajteh.configma.ConfigFactory;
import dev.kajteh.configma.json.JsonConfigParser;

import java.nio.file.Paths;

public class ExampleMain {

    public static void main(String[] args) {
        var config = ConfigFactory.builder(ExampleConfig.class)
                .format(JsonConfigParser.standard())
                .file(Paths.get("test", "cfg.json"))
                .build();

        System.out.println(config.get().testList);
    }
}
