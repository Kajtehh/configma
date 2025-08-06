package pl.kajteh.configma.serialization.impl.datetime;

import pl.kajteh.configma.serialization.ConfigSerializer;

import java.time.Instant;

public class InstantSerializer implements ConfigSerializer<Instant> {

    @Override
    public Class<?> getTargetType() {
        return Instant.class;
    }

    @Override
    public Object serialize(Instant value) {
        return value.toString();
    }

    @Override
    public Instant deserialize(Class<Instant> type, Object value) {
        if (value instanceof String) {
            return Instant.parse((String) value);
        }

        if (value instanceof Long) {
            return Instant.ofEpochMilli((long) value);
        }

        throw new IllegalArgumentException("Cannot deserialize Instant from: " + value);
    }
}