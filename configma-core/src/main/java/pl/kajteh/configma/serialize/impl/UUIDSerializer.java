package pl.kajteh.configma.serialize.impl;

import pl.kajteh.configma.serialize.ConfigSerializer;

import java.util.UUID;

public class UUIDSerializer implements ConfigSerializer<UUID> {

    @Override
    public Class<?> getTargetType() {
        return UUID.class;
    }

    @Override
    public Object serialize(UUID uuid) {
        return uuid.toString();
    }

    @Override
    public UUID deserialize(Class<UUID> type, Object value) {
        return UUID.fromString((String) value);
    }
}
