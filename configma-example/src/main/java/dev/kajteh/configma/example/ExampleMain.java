package dev.kajteh.configma.example;

import dev.kajteh.configma.ConfigFactory;
import dev.kajteh.configma.example.user.UserSerializer;
import dev.kajteh.configma.yaml.YamlConfigParser;

import java.nio.file.Paths;

public class ExampleMain {

    public static void main(String[] args) {
        final var config = ConfigFactory.builder(ExampleConfig.class)
                .file(Paths.get("test", "test.yml"))
                .parser(YamlConfigParser.standard())
                .serializer(new UserSerializer())
                .build();

        config.get(cfg -> System.out.println(cfg.productionMode));
    }
}
