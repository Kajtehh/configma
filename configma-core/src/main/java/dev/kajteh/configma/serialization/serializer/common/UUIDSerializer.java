package dev.kajteh.configma.serialization.serializer.common;

import dev.kajteh.configma.serialization.serializer.ValueSerializer;

import java.util.UUID;

public class UUIDSerializer implements ValueSerializer<UUID> {

    @Override
    public Object serialize(final UUID uuid) {
        return uuid.toString();
    }

    @Override
    public UUID deserialize(final Object raw) {
        try {
            return UUID.fromString(raw.toString());
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Failed to deserialize value to UUID: '" + raw + "'", e
            );
        }
    }

    @Override
    public boolean matches(final Class<?> type) {
        return UUID.class.isAssignableFrom(type);
    }
}
