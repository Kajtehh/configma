package dev.kajteh.configma.example;

import dev.kajteh.configma.ConfigFactory;
import dev.kajteh.configma.example.user.UserSerializer;
import dev.kajteh.configma.yaml.YamlConfigParser;

public class ExampleMain {

    public static void main(String[] args) {
        final var config = ConfigFactory.builder(ExampleConfig.class)
                .file("config.yml")
                .parser(YamlConfigParser.standard())
                .serializer(new UserSerializer())
                .build();

        config.get(cfg -> cfg.users.forEach(System.out::println));
    }
}
