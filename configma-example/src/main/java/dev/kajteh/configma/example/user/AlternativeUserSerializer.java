package dev.kajteh.configma.example.user;

import dev.kajteh.configma.serialization.serializer.TypeSerializer;

import java.util.UUID;

public class AlternativeUserSerializer implements TypeSerializer<User, String> {

    @Override
    public String serialize(User user) {
        return user.id() + "," + user.name() + "," + user.email();
    }

    @Override
    public User deserialize(String string) {
        final var split = string.split(",");
        return new User(
                UUID.fromString(split[0]),
                split[1],
                split[2]
        );
    }

    @Override
    public boolean matches(Class<?> type) {
        return User.class.isAssignableFrom(type);
    }
}
