package pl.kajteh.configma.serialization.data;

import pl.kajteh.configma.ConfigProcessor;

import java.util.*;

public class SerializedData {

    private final ConfigProcessor processor;
    private final Map<String, Object> values;

    public SerializedData(ConfigProcessor processor, Map<String, Object> values) {
        this.processor = processor;
        this.values = values;
    }

    public Object getRaw(String key) {
        return values.get(key);
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    private <T> T getProcessed(String key, Class<?> mainType, Class<?>... genericTypes) {
        final Object rawValue = getRaw(key);

        return (T) processor.processExisting(mainType, rawValue, List.of(genericTypes));
    }

    public <T> T get(String key, Class<T> type) {
        return this.getProcessed(key, type);
    }

    public <T> Collection<T> getAsCollection(String key, Class<T> elementType) {
        return this.getProcessed(key, Collection.class, elementType);
    }

    public <T> List<T> getAsList(String key, Class<T> elementType) {
        return this.getProcessed(key, Collection.class, elementType);
    }

    public <T> Set<T> getAsSet(String key, Class<T> elementType) {
        return this.getProcessed(key, Collection.class, elementType);
    }

    public <K, V> Map<K, V> getAsMap(String key, Class<K> keyType, Class<V> valueType) {
        return this.getProcessed(key, Map.class, keyType, valueType);
    }
}

