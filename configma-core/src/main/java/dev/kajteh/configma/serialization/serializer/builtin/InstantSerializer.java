package dev.kajteh.configma.serialization.serializer.builtin;

import dev.kajteh.configma.serialization.serializer.TypeSerializer;

import java.time.Instant;

public class InstantSerializer implements TypeSerializer<Instant, String> {

    @Override
    public String serialize(final Instant instant) {
        return instant.toString();
    }

    @Override
    public Instant deserialize(final String string) {
        try {
            return Instant.parse(string);
        } catch (final Exception e) {
            throw new IllegalArgumentException(
                    "Cannot deserialize value to Instant: '" + string + "'", e
            );
        }
    }

    @Override
    public boolean matches(final Class<?> type) {
        return Instant.class.isAssignableFrom(type);
    }
}
