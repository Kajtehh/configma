package dev.kajteh.configma.serialization.serializer.common;

import dev.kajteh.configma.serialization.serializer.TypeSerializer;

import java.time.Instant;

public class InstantSerializer implements TypeSerializer<Instant> {

    @Override
    public Object serialize(final Instant instant) {
        return instant.toString();
    }

    @Override
    public Instant deserialize(final Object raw) {
        try {
            return Instant.parse(raw.toString());
        } catch (final Exception e) {
            throw new IllegalArgumentException(
                    "Cannot deserialize value to Instant: '" + raw + "'", e
            );
        }
    }

    @Override
    public boolean matches(final Class<?> type) {
        return Instant.class.isAssignableFrom(type);
    }
}
