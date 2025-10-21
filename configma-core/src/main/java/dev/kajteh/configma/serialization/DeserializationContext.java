package dev.kajteh.configma.serialization;

import java.lang.reflect.Type;
import java.util.Map;

public class DeserializationContext {

    private final SerializationService serializationService;
    private final Map<String, Object> values;

    public DeserializationContext(final SerializationService serializationService, final Map<String, Object> values) {
        this.serializationService = serializationService;
        this.values = values;
    }

    public boolean has(final String key) {
        return this.values.containsKey(key);
    }

    public <T> T get(final String key, final Type type) {
        return this.serializationService.deserializeValue(this.values.get(key), type);
    }

    public <T> T get(final String key, final Type type, final T defaultValue) {
        final Object raw = this.values.get(key);

        if (raw == null) return defaultValue;

        return this.serializationService.deserializeValue(raw, type);
    }
}  