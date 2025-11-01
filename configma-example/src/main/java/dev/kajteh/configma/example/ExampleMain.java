package dev.kajteh.configma.example;

import dev.kajteh.configma.ConfigFactory;
import dev.kajteh.configma.example.user.UserSerializer;
import dev.kajteh.configma.yaml.YamlConfigParser;

import java.io.File;

public class ExampleMain {

    public static void main(final String[] args) {
        final var exampleConfig = ConfigFactory.create(ExampleConfig.class, builder -> {
            builder.serializer(new UserSerializer());
            builder.parser(new YamlConfigParser());
            builder.file(new File("config.yml"));
        });

        exampleConfig.get(config -> config.users.forEach(System.out::println));
    }
}
