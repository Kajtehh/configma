package pl.kajteh.configma.serialization.serializer.impl;

import pl.kajteh.configma.serialization.serializer.ValueSerializer;

import java.util.UUID;

public class UUIDSerializer implements ValueSerializer<UUID> {

    @Override
    public Object serialize(final UUID uuid) {
        return uuid.toString();
    }

    @Override
    public UUID deserialize(final Object raw) {
        if (!(raw instanceof String uuidString)) {
            throw new IllegalArgumentException("Expected String for UUID deserialization, got: " + raw.getClass());
        }

        return UUID.fromString(uuidString);
    }

    @Override
    public Class<UUID> getType() {
        return UUID.class;
    }
}
