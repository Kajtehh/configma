package pl.kajteh.configma.serialization;

import pl.kajteh.configma.ConfigProcessor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SerializedData {

    private final ConfigProcessor processor;
    private final Map<String, Object> values;

    public SerializedData(final ConfigProcessor processor, final Map<String, Object> values) {
        this.processor = processor;
        this.values = values;
    }

    public Object getRaw(final String key) {
        return this.values.get(key);
    }

    public boolean has(final String key) {
        return this.values.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    private <T> T getProcessed(final String key, final Class<?> mainType, final Class<?>... genericTypes) {
        final Object rawValue = getRaw(key);

        return (T) this.processor.processExisting(mainType, rawValue, List.of(genericTypes));
    }

    public <T> T get(final String key, final Class<T> type) {
        return this.getProcessed(key, type);
    }

    public <T> Collection<T> getAsCollection(final String key, final Class<T> elementType) {
        return this.getProcessed(key, Collection.class, elementType);
    }

    public <T> List<T> getAsList(final String key, final Class<T> elementType) {
        return this.getProcessed(key, Collection.class, elementType);
    }

    public <T> Set<T> getAsSet(final String key, final Class<T> elementType) {
        return this.getProcessed(key, Collection.class, elementType);
    }

    public <K, V> Map<K, V> getAsMap(final String key, final Class<K> keyType, final Class<V> valueType) {
        return this.getProcessed(key, Map.class, keyType, valueType);
    }
}