package dev.kajteh.configma.serialization;

import dev.kajteh.configma.serialization.helper.GenericType;

import java.lang.reflect.Type;
import java.util.List;
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

    public <E> List<E> getList(final String key, final Class<E> elementType) {
        return this.get(key, GenericType.of(List.class, elementType));
    }

    public <K, V> Map<K, V> getMap(final String key, final Class<K> keyType, final Class<V> valueType) {
        return this.get(key, GenericType.of(Map.class, keyType, valueType));
    }

    public String getString(final String key) {
        return this.get(key, String.class);
    }
}  