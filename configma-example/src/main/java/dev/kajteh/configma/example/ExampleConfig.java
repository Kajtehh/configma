package dev.kajteh.configma.example;

import dev.kajteh.configma.example.user.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ExampleConfig {

    public List<User> users = List.of(
            new User(
                    UUID.randomUUID(),
                    "User 1",
                    "test@kajteh.dev",
                    Instant.now()
            )
    );
}
