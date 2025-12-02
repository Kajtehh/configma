package dev.kajteh.configma;

import dev.kajteh.configma.annotation.*;
import dev.kajteh.configma.exception.ConfigException;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigSchema<T> {

    private static final Map<Class<?>, ConfigField[]> FIELD_CACHE = new ConcurrentHashMap<>();

    private final T instance;
    private final ConfigField[] fields;

    private ConfigSchema(final Class<?> type, final T instance) {
        this.instance = instance;
        this.fields = FIELD_CACHE.computeIfAbsent(type, this::scanFields);
    }

    public static <T> ConfigSchema<T> of(final Class<T> type, final T instance) {
        return new ConfigSchema<>(type, instance != null ? instance : createInstance(type));
    }

    public static ConfigSchema<?> ofNested(final ConfigField field, final Object parentInstance) {
        return new ConfigSchema<>(field.type(), field.getValue(parentInstance));
    }

    private ConfigField[] scanFields(final Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()))
                .filter(f -> !f.isAnnotationPresent(Exclude.class))
                .map(ConfigField::of)
                .toArray(ConfigField[]::new);
    }

    private static <T> T createInstance(final Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (final Exception e) {
            throw new ConfigException("Cannot create config instance: " + type.getName(), e);
        }
    }

    public T instance() {
        return this.instance;
    }

    public ConfigField[] fields() {
        return this.fields;
    }
}
