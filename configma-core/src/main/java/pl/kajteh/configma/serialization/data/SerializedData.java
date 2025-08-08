package pl.kajteh.configma.serialization.data;

import pl.kajteh.configma.ConfigProcessor;

import java.util.*;

public class SerializedData {

    private final ConfigProcessor processor;
    private final Map<String, Object> values;
    private final String path;

    public SerializedData(ConfigProcessor processor, Map<String, Object> values, String path) {
        this.processor = processor;
        this.values = values;
        this.path = path;
    }

    public Object getRaw(String key) {
        return this.values.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        final Object rawValue = this.getRaw(key);

        return (T) this.processor.processExisting(this.path + ".key", rawValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        final Object rawValue = this.getRaw(key);

        if (rawValue == null) return defaultValue;

        return (T) this.processor.processExisting(this.path + ".key", rawValue);
    }

    // zrob metode get as collection itp i tam dodaj parametr zeby podac typ i musisz w processor zrobic taka funkcje do kolekcji i map gdzie bedzie taki param

    public boolean has(String key) {
        return this.values.containsKey(key);
    }
}
