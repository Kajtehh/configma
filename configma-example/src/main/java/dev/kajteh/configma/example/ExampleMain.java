package dev.kajteh.configma.example;

import dev.kajteh.configma.ConfigFactory;
import dev.kajteh.configma.example.user.UserSerializer;
import dev.kajteh.configma.yaml.YamlConfigParser;

import java.io.File;

public class ExampleMain {

    public static void main(String[] args) {
        final var exampleConfig = ConfigFactory.create(ExampleConfig.class, b -> {
            b.parser(new YamlConfigParser());
            b.serializer(new UserSerializer());
            b.file(new File("config.yml"));
        });

        exampleConfig.get(config -> config.users.forEach(System.out::println));
    }
}
