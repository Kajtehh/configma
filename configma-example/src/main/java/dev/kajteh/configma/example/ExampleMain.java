package dev.kajteh.configma.example;

import dev.kajteh.configma.ConfigFactory;
import dev.kajteh.configma.example.user.User;
import dev.kajteh.configma.example.user.UserSerializer;
import dev.kajteh.configma.yaml.YamlConfigParser;

import java.nio.file.Paths;
import java.util.UUID;

public class ExampleMain {

    public static void main(String[] args) {
        final var config = ConfigFactory.builder(ExampleConfig.class)
                .file(Paths.get("test", UUID.randomUUID() + ".yml"))
                .parser(YamlConfigParser.standard())
                .serializer(new UserSerializer())
                .build();

        config.get(cfg -> cfg.users.forEach(System.out::println));

        System.out.println("---------------------");

        config.edit(cfg -> cfg.users.add(new User(UUID.randomUUID(), "test user", "test@kajteh.dev")));
        config.save();

        config.get(cfg -> cfg.users.forEach(System.out::println));
    }
}
