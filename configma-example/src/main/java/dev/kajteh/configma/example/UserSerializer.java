package dev.kajteh.configma.example;

import dev.kajteh.configma.serialization.context.DeserializationContext;
import dev.kajteh.configma.serialization.context.SerializationContext;
import dev.kajteh.configma.serialization.serializer.ObjectSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UserSerializer implements ObjectSerializer<User> {

    @Override
    public void serialize(@NotNull SerializationContext context, @NotNull User user) {
        context.set("id", user.id());
        context.set("name", user.name());
    }

    @Override
    public User deserialize(@NotNull DeserializationContext context) {
        return new User(
                context.get("id", UUID.class),
                context.getString("name")
        );
    }

    @Override
    public boolean matches(@NotNull Class<?> type) {
        return User.class.isAssignableFrom(type);
    }
}
