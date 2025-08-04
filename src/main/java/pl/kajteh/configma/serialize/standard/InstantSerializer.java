package pl.kajteh.configma.serialize.standard;

import pl.kajteh.configma.serialize.ConfigSerializer;

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
        if (value instanceof String str) {
            return Instant.parse(str);
        }

        if (value instanceof Long epochMillis) {
            return Instant.ofEpochMilli(epochMillis);
        }

        throw new IllegalArgumentException("Cannot deserialize Instant from: " + value);
    }
}