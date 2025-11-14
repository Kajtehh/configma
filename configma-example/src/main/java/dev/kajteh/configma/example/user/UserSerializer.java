package dev.kajteh.configma.example.user;

import dev.kajteh.configma.serialization.DeserializationContext;
import dev.kajteh.configma.serialization.SerializationContext;
import dev.kajteh.configma.serialization.serializer.ObjectSerializer;

import java.util.UUID;

public class UserSerializer implements ObjectSerializer<User> {

    @Override
    public void serialize(SerializationContext context, User user) {
        context.set("id", user.id());
        context.set("name", user.name());
        context.set("email", user.email());
    }

    @Override
    public User deserialize(DeserializationContext context) {
        return new User(
                context.get("id", UUID.class),
                context.get("name", String.class),
                context.get("email", String.class)
        );
    }

    @Override
    public boolean matches(Class<?> type) {
        return User.class.isAssignableFrom(type);
    }
}
