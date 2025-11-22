package dev.kajteh.configma.serialization.serializer.builtin;

import dev.kajteh.configma.serialization.serializer.TypeSerializer;

import java.util.UUID;

public class UUIDSerializer implements TypeSerializer<UUID, String> {

    @Override
    public String serialize(final UUID uuid) {
        return uuid.toString();
    }

    @Override
    public UUID deserialize(final String string) {
        try {
            return UUID.fromString(string);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Failed to deserialize value to UUID: '" + string + "'", e
            );
        }
    }

    @Override
    public boolean matches(final Class<?> type) {
        return UUID.class.isAssignableFrom(type);
    }
}
