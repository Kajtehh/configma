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
        this.schema = ConfigSchema.of(type, instance);
        this.context = ConfigContext.of(type);
        this.serializer = new SerializationService(serializers);

        this.registerComments(this.schema, null, this.context);
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

                this.parser.write(writer, toWrite, this.context);

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

            if (field.isNested()) {
                final Map<String, Object> subLoaded =
                        loadedValues.containsKey(field.name())
                                ? (Map<String, Object>) loadedValues.get(formattedName)
                                : Map.of();

                final var nestedSchema = field.nestedSchema(instance);
                final var subWrite = this.loadSchema(nestedSchema, subLoaded, write);

                field.setValue(instance, nestedSchema.instance());

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

            this.parser.write(writer, toWrite, this.context);

        } catch (final IOException e) {
            throw new ConfigException("Failed to save configuration to file: " + this.file.getAbsolutePath(), e);
        }
    }

    private Map<String, Object> saveSchema(final ConfigSchema<?> schema) {
        final Map<String, Object> out = new LinkedHashMap<>();

        for (final var field : schema.fields()) {

            final var formattedName = this.parser.formatField(field.name());

            out.put(formattedName, field.isNested()
                    ? this.saveSchema(field.nestedSchema(field.getValue(schema.instance())))
                    : this.serializer.serializeValue(field.getValue(schema.instance()), field.genericType()));
        }

        return out;
    }

    private void registerComments(final ConfigSchema<?> schema, final String parentPath, final ConfigContext context) {
        for (final var field : schema.fields()) {
            final var path = this.parser.formatField(
                    parentPath != null ? parentPath + "." + field.name() : field.name()
            );

            if (field.comments() != null)
                context.comments().put(path, field.comments());

            if (field.inlineComment() != null)
                context.inlineComments().put(path, field.inlineComment());

            if (field.isNested())
                this.registerComments(field.nestedSchema(schema.instance()), path, context);
        }
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