package dev.kajteh.configma;

import dev.kajteh.configma.annotation.*;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigSchema<T> {

    private static final Map<Class<?>, ConfigField[]> FIELD_CACHE = new ConcurrentHashMap<>();

    private final T instance;
    private final ConfigField[] fields;

    public ConfigSchema(final Class<T> type) {
        this(type, null);
    }

    public ConfigSchema(final Class<T> type, final T instance) {
        this.instance = instance != null ? instance : this.createInstance(type);
        this.fields = FIELD_CACHE.computeIfAbsent(type, this::scanFields);
    }

    public T instance() {
        return this.instance;
    }

    public ConfigField[] fields() {
        return this.fields;
    }

    private ConfigField[] scanFields(final Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()))
                .filter(f -> !f.isAnnotationPresent(Exclude.class))
                .map(ConfigField::of)
                .toArray(ConfigField[]::new);
    }

    private T createInstance(final Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (final Exception e) {
            throw new ConfigException("Cannot create config instance: " + type.getName(), e);
        }
    }
}
