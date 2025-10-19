package dev.kajteh.configma.serialization;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class SerializationContext {

    private final Map<String, Object> values = new LinkedHashMap<>();
    private final SerializationService serializationService;

    public SerializationContext(final SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    public <T> void set(final String key, final T value) {
        this.set(key, value, value.getClass());
    }

    public <T> void set(final String key, final T value, final Type type) {
        this.values.put(key, this.serializationService.serializeValue(value, type));
    }

    protected Map<String, Object> values() {
        return this.values;
    }
}
