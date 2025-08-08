package pl.kajteh.configma.type;

import java.util.*;

public class ConfigTypeCache {

    private final Map<String, Class<?>> types = new HashMap<>();

    public void putType(final String path, final Class<?> type) {
        this.types.put(path, type);
    }

    public Class<?> getType(final String path) {
        return this.types.get(path);
    }

    public boolean contains(final String path) {
        return this.types.containsKey(path);
    }

    public void clear() {
        this.types.clear();
    }
}
