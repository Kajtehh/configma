package dev.kajteh.configma.serialization;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
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
        this.values.put(key, value != null
                ? this.serializationService.serializeValue(value, type)
                : null);
    }

    public <E> void setList(final String key, final Collection<E> list, final Class<E> elementType) {
        this.set(key, list, GenericType.of(Collection.class, elementType));
    }

    public <K, V> void setMap(final String key, final Map<K, V> map, final Class<K> keyType, final Class<V> valueType) {
        this.set(key, map, GenericType.of(Map.class, keyType, valueType));
    }

    public Map<String, Object> values() {
        return Collections.unmodifiableMap(this.values);
    }
}
