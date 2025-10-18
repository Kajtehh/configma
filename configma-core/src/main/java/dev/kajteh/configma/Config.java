package dev.kajteh.configma;

import dev.kajteh.configma.serialization.SerializationService;
import dev.kajteh.configma.serialization.serializer.Serializer;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;

public final class Config<T> {

    private final Class<T> type;
    private final T instance;
    private final File file;
    private final ConfigAdapter adapter;
    private final SerializationService serializer;

    Config(
            final ConfigAdapter adapter,
            final Class<T> type,
            final T instance,
            final File file,
            final List<Serializer<?>> serializers
    ) {
        this.type = type;
        this.instance = instance;
        this.file = file;
        this.adapter = adapter;
        this.serializer = new SerializationService(serializers);
    }

    void load(final boolean write) {
        try (final var reader = new FileReader(this.file)) {
            final var loadedValues = Optional.ofNullable(this.adapter.load(reader))
                    .orElseGet(LinkedHashMap::new);

            final Map<String, Object> valuesToWrite = new LinkedHashMap<>();

            for (final var field : this.getSerializableFields()) {
                field.setAccessible(true);

                final String name = this.formatFieldName(field.getName());
                final Object value = loadedValues.containsKey(name)
                        ? this.serializer.deserializeValue(loadedValues.get(name), field.getGenericType())
                        : field.get(this.instance);

                field.set(this.instance, value);
                valuesToWrite.put(name, this.serializer.serializeValue(value, field.getGenericType()));
            }

            if (write && !valuesToWrite.isEmpty()) {
                try (final var writer = new FileWriter(this.file)) {
                    this.adapter.write(writer, valuesToWrite);
                }
            }
        } catch (final IOException | IllegalAccessException e) {
            throw new ConfigException(e);
        }
    }

    public void save() {
        try (final var writer = new FileWriter(this.file)) {
            final Map<String, Object> valuesToWrite = new LinkedHashMap<>();

            for (final var field : this.getSerializableFields()) {
                final String name = this.formatFieldName(field.getName());
                final Object value = field.get(this.instance);
                valuesToWrite.put(name, this.serializer.serializeValue(value, field.getGenericType()));
            }

            this.adapter.write(writer, valuesToWrite);
        } catch (final IOException | IllegalAccessException e) {
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
        this.save();
    }

    private String formatFieldName(final String name) {
        final var namingStyle = this.adapter.getNamingStyle();
        return namingStyle == null ? name : namingStyle.format(name);
    }

    private Field[] getSerializableFields() {
        return Arrays.stream(this.type.getDeclaredFields())
                .filter(field -> !Modifier.isFinal(field.getModifiers()))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .toArray(Field[]::new);
    }
}
