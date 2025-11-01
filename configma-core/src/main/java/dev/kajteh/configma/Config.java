package dev.kajteh.configma;

import dev.kajteh.configma.serialization.SerializationService;
import dev.kajteh.configma.serialization.serializer.Serializer;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class Config<T> {

    private static final Map<Class<?>, Field[]> FIELD_CACHE = new ConcurrentHashMap<>();

    private final T instance;
    private final File file;
    private final ConfigParser parser;
    private final SerializationService serializer;
    private final Field[] serializableFields;

    Config(
            final ConfigParser adapter,
            final Class<T> type,
            final T instance,
            final File file,
            final List<Serializer<?>> serializers
    ) {
        this.instance = instance;
        this.file = file;
        this.parser = adapter;
        this.serializer = new SerializationService(serializers);

        this.serializableFields = FIELD_CACHE.computeIfAbsent(type, t ->
                Arrays.stream(t.getDeclaredFields())
                        .filter(field -> !Modifier.isFinal(field.getModifiers())
                                && !Modifier.isTransient(field.getModifiers()))
                        .peek(field -> field.setAccessible(true))
                        .toArray(Field[]::new)
        );
    }

    void load(final boolean write) {
        final Map<String, Object> loadedValues;
        try (final var reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(this.file), StandardCharsets.UTF_8))) {
            loadedValues = Optional.ofNullable(parser.load(reader))
                    .orElseGet(LinkedHashMap::new);
        } catch (final IOException e) {
            throw new ConfigException(e);
        }

        final Map<String, Object> toWrite = write ? new LinkedHashMap<>() : null;

        for (final var field : this.serializableFields) {
            final String name = this.parser.formatField(field.getName());
            final var type = field.getGenericType();

            try {
                final Object value = loadedValues.containsKey(name)
                        ? this.serializer.deserializeValue(loadedValues.get(name), type)
                        : field.get(instance);

                field.set(this.instance, value);

                if (write) toWrite.put(name, this.serializer.serializeValue(value, type));
            } catch (final IllegalAccessException e) {
                throw new ConfigException(e);
            }
        }

        if (write && !toWrite.isEmpty()) {
            try (final var writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8))) {
                parser.write(writer, toWrite);
            } catch (final IOException e) {
                throw new ConfigException(e);
            }
        }
    }

    public void save() {
        final Map<String, Object> toWrite = new LinkedHashMap<>();
        try {
            for (final var field : this.serializableFields) {
                final String name = this.parser.formatField(field.getName());
                toWrite.put(name, this.serializer.serializeValue(field.get(this.instance), field.getGenericType()));
            }
        } catch (final IllegalAccessException e) {
            throw new ConfigException(e);
        }

        try (final var writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8))) {
            this.parser.write(writer, toWrite);
        } catch (final IOException e) {
            throw new ConfigException(e);
        }
    }

    public void reload() {
        this.load(false);
    }

    public T get() {
        return this.instance;
    }

    public void get(final Consumer<T> instanceConsumer) {
        instanceConsumer.accept(this.instance);
    }

    public void edit(final Consumer<T> instanceConsumer) {
        instanceConsumer.accept(this.instance);
    }
}
