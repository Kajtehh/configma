package pl.kajteh.configma.serialization.serializer.impl;

import pl.kajteh.configma.serialization.serializer.ValueSerializer;

import java.time.Instant;

public class InstantSerializer implements ValueSerializer<Instant> {

    @Override
    public Object serialize(final Instant instant) {
        return instant.toString();
    }

    @Override
    public Instant deserialize(final Object raw) {
        if (raw instanceof String str) {
            return Instant.parse(str);
        }

        if (raw instanceof Long l) {
            return Instant.ofEpochMilli(l);
        }

        throw new IllegalArgumentException("Cannot deserialize Instant from: " + raw.getClass().getName());
    }

    @Override
    public Class<Instant> getType() {
        return Instant.class;
    }
}