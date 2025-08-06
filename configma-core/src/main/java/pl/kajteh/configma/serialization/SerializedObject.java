package pl.kajteh.configma.serialization;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class SerializedObject {

    private final Map<String, Object> serializedObject;

    public SerializedObject() {
        this.serializedObject = new HashMap<>();
    }

    public SerializedObject(Object object) {
        if (!(object instanceof Map)) {
            throw new IllegalArgumentException("Object must be a Map");
        }
        this.serializedObject = new HashMap<>((Map<String, Object>) object);
    }

    public <T> void set(String key, T value) {
        Objects.requireNonNull(key, "Key cannot be null");
        serializedObject.put(key, value);
    }

    public <T> T get(String key) {
        return (T) serializedObject.get(key);
    }

    public <T> T get(String key, T defaultValue) {
        final T value = get(key);
        return value != null ? value : defaultValue;
    }

    public boolean has(String key) {
        return serializedObject.containsKey(key);
    }

    public void putAll(Map<String, Object> map) {
        serializedObject.putAll(map);
    }

    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(serializedObject);
    }
}
