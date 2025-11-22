package dev.kajteh.configma;

import dev.kajteh.configma.serialization.SerializationService;
import dev.kajteh.configma.serialization.serializer.Serializer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

public final class Config<T> {

    private final File file;
    private final ConfigParser parser;
    private final ConfigSchema<T> schema;
    private final ConfigContext context;
    private final SerializationService serializer;

    Config(
            final File file,
            final ConfigParser parser,
            final Class<T> type,
            final T instance,
            final List<Serializer<?, ?>> serializers
    ) {
        this.file = file;
        this.parser = parser;
        this.schema = new ConfigSchema<>(type, instance);
        this.context = ConfigContext.of(type);
        this.serializer = new SerializationService(serializers);
    }

    void load(final boolean write) {
        final Map<String, Object> loadedValues;

        try (final var reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(this.file), StandardCharsets.UTF_8))) {

            loadedValues = Optional.ofNullable(this.parser.load(reader))
                    .orElseGet(LinkedHashMap::new);

        } catch (final IOException e) {
            throw new ConfigException("Failed to load configuration file: " + this.file.getAbsolutePath(), e);
        }

        final var toWrite = this.loadSchema(this.schema, loadedValues, write);

        if (write && !toWrite.isEmpty()) {
            try (final var writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8))) {
                this.parser.write(writer, this.context, toWrite);
            } catch (final IOException e) {
                throw new ConfigException("Failed to write configuration file: " + this.file.getAbsolutePath(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadSchema(
            final ConfigSchema<?> schema,
            final Map<String, Object> loadedValues,
            final boolean write
    ) {
        final var instance = schema.instance();
        final Map<String, Object> toWrite = write ? new LinkedHashMap<>() : null;

        for (final var field : schema.fields()) {

            final var formattedName = this.parser.formatField(field.name());
            this.applyComments(field, formattedName);

            if (field.isNestedConfig()) {
                final Map<String, Object> subLoaded =
                        loadedValues.containsKey(field.name())
                                ? (Map<String, Object>) loadedValues.get(formattedName)
                                : Map.of();

                final var subWrite = this.loadSchema(field.nestedSchema(), subLoaded, write);

                field.setValue(instance, field.nestedSchema().instance());

                if (write)
                    toWrite.put(formattedName, subWrite);

                continue;
            }

            final var sourceValue = loadedValues.get(formattedName);

            final var value = sourceValue != null
                    ? this.serializer.deserializeValue(sourceValue, field.genericType())
                    : field.getValue(instance);

            field.setValue(instance, value);

            if (write)
                toWrite.put(formattedName, this.serializer.serializeValue(value, field.genericType()));
        }

        return toWrite;
    }

    public void save() {
        final var toWrite = this.saveSchema(this.schema);

        try (final var writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8))) {

            this.parser.write(writer, this.context, toWrite);

        } catch (final IOException e) {
            throw new ConfigException("Failed to save configuration to file: " + this.file.getAbsolutePath(), e);
        }
    }

    private Map<String, Object> saveSchema(final ConfigSchema<?> schema) {
        final Map<String, Object> out = new LinkedHashMap<>();

        for (final var field : schema.fields()) {

            final var formattedName = this.parser.formatField(field.name());

            out.put(formattedName, field.isNestedConfig()
                    ? this.saveSchema(field.nestedSchema())
                    : this.serializer.serializeValue(field.getValue(schema.instance()), field.genericType()));
        }

        return out;
    }

    private void applyComments(final ConfigField field, final String formattedName) {
        if(!this.parser.commentsSupported()) return;

        if(field.comments() != null)
            this.context.comments().put(formattedName, field.comments());

        if(field.inlineComment() != null)
            this.context.inlineComments().put(formattedName, field.inlineComment());
    }

    public void reload() {
        this.load(false);
    }

    public T get() {
        return this.schema.instance();
    }

    public void get(final Consumer<T> instanceConsumer) {
        instanceConsumer.accept(this.schema.instance());
    }

    public void edit(final Consumer<T> instanceConsumer) {
        instanceConsumer.accept(this.schema.instance());
    }
}