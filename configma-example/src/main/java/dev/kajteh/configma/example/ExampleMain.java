package dev.kajteh.configma.example;

import dev.kajteh.configma.ConfigFactory;
import dev.kajteh.configma.example.user.UserSerializer;
import dev.kajteh.configma.yaml.YamlConfigParser;

import java.nio.file.Paths;

public class ExampleMain {

    public static void main(String[] args) {
        var config = ConfigFactory.builder(ExampleConfig.class)
                .parser(YamlConfigParser.standard())
                .file(Paths.get("test", "test.yml"))
                .serializer(new UserSerializer())
                .build();
    }
}
