package pl.kajteh.configma.serializer;

import java.util.Map;

@SuppressWarnings("unchecked")
public class SerializedObject {

    private final Map<?, ?> serializedObject;

    public SerializedObject(Object object) {
        this.serializedObject = (Map<?, ?>) object;
    }

    public <T> T get(String key) {
        return (T) this.serializedObject.get(key);
    }

    public <T> T get(String key, T defaultValue) {
        final T value = this.get(key);
        return value != null ? value : defaultValue;
    }

    public boolean contains(String key) {
        return this.serializedObject.containsKey(key);
    }
}
