package pl.kajteh.configma.serialization.data;

import pl.kajteh.configma.ConfigProcessor;

import java.util.LinkedHashMap;
import java.util.Map;

public class SerializationData {

    private final Map<String, Object> values = new LinkedHashMap<>();
    private final ConfigProcessor processor;

    public SerializationData(ConfigProcessor processor) {
        this.processor = processor;
    }

    public <T> void add(String key, T value) {
        this.add(key, value, value.getClass());
    }

    public <T> void add(String key, T value, Class<?> type) {
        this.values.put(key, this.processor.process(type, value));
    }

    public Map<String, Object> asMap() {
        return this.values;
    }
}
