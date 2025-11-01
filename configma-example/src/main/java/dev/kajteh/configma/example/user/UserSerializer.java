package dev.kajteh.configma.example.user;

import dev.kajteh.configma.serialization.DeserializationContext;
import dev.kajteh.configma.serialization.SerializationContext;
import dev.kajteh.configma.serialization.serializer.ObjectSerializer;

import java.time.Instant;
import java.util.UUID;

public class UserSerializer implements ObjectSerializer<User> {

    @Override
    public void serialize(SerializationContext context, User user) {
        context.set("id", user.id());
        context.set("name", user.name());
        context.set("email", user.email());

        if(user.emailVerifiedAt() != null) context.set("email-verified-at", user.emailVerifiedAt());
    }

    @Override
    public User deserialize(DeserializationContext context) {
        return new User(
                context.get("id", UUID.class),
                context.get("name", String.class),
                context.get("email", String.class),
                context.get("email-verified-at", Instant.class)
        );
    }

    @Override
    public boolean matches(Class<?> type) {
        return User.class.isAssignableFrom(type);
    }
}
