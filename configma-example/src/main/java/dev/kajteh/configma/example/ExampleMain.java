package dev.kajteh.configma.example;

import dev.kajteh.configma.Config;
import dev.kajteh.configma.ConfigFactory;
import dev.kajteh.configma.json.JsonConfigLoader;
import dev.kajteh.configma.serialization.serializer.builtin.RecordSerializer;
import dev.kajteh.configma.yaml.YamlConfigLoader;

import java.nio.file.Paths;
import java.util.Set;
import java.util.logging.Logger;

public final class ExampleMain {

    private static final Logger LOGGER = Logger.getLogger(ExampleMain.class.getName());

    public static void main(String[] args) {
        final var builder = ConfigFactory.builder(ExampleSettings.class)
                .path(Paths.get("test", "test.config"))
                .serializer(new RecordSerializer());

        final Set<Config<ExampleSettings>> configs = Set.of(
                builder.load(YamlConfigLoader.createDefault()),
                builder.load(JsonConfigLoader.createDefault())
        );

        configs.forEach(config -> LOGGER.info(config.get().toString()));
    }
}
