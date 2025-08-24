package pl.kajteh.configma.serialization;

import pl.kajteh.configma.ConfigProcessor;

import java.util.LinkedHashMap;
import java.util.Map;

public class SerializationData {

    private final Map<String, Object> values = new LinkedHashMap<>();
    private final ConfigProcessor processor;

    public SerializationData(final ConfigProcessor processor) {
        this.processor = processor;
    }

    public <T> void add(final String key, final T value) {
        this.add(key, value, value.getClass());
    }

    public <T> void add(final String key, final T value, final Class<?> type) {
        this.values.put(key, this.processor.process(type, value));
    }

    protected Map<String, Object> asMap() {
        return this.values;
    }
}