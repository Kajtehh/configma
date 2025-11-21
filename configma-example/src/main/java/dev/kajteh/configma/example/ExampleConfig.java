package dev.kajteh.configma.example;

import dev.kajteh.configma.annotation.NestedConfig;
import dev.kajteh.configma.example.user.User;

import java.util.List;
import java.util.UUID;

public class ExampleConfig {

    public List<User> users = List.of(
            new User(
                    UUID.randomUUID(),
                    "Kajteh",
                    "me@kajteh.dev"
            )
    );

    @NestedConfig
    public DatabaseConfig database = new DatabaseConfig();

    public static class DatabaseConfig {
        public String host = "localhost";
        public int port = 5432;
        public String user = "root";
        public String password = "secret";
    }
}
