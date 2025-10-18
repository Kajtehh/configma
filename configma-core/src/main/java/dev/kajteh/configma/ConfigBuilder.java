package dev.kajteh.configma;

import dev.kajteh.configma.serialization.serializer.Serializer;
import dev.kajteh.configma.serialization.serializer.common.InstantSerializer;
import dev.kajteh.configma.serialization.serializer.common.UUIDSerializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ConfigBuilder<T> {

    private static final List<Serializer<?>> COMMON_SERIALIZERS = List.of(
            new UUIDSerializer(),
            new InstantSerializer()
    );

    private final Class<T> type;
    private final T instance;
    private final List<Serializer<?>> customSerializers = new ArrayList<>();

    private ConfigAdapter adapter;
    private File file;

    public ConfigBuilder(final Class<T> type) {
        this(type, ConfigInstanceFactory.createInstance(type));
    }

    public ConfigBuilder(final Class<T> type, final T instance) {
        this.type = type;
        this.instance = instance;
    }

    public ConfigBuilder<T> file(final File file) {
        this.file = file;
        return this;
    }

    public ConfigBuilder<T> adapter(final ConfigAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public ConfigBuilder<T> serializer(final Serializer<?>... serializers) {
        this.customSerializers.addAll(List.of(serializers));
        return this;
    }

    public Config<T> initialize() {
        if (this.file == null) {
            throw new ConfigException("Config file cannot be null");
        }

        this.ensureFileExists(file);

        final var serializers = new ArrayList<>(COMMON_SERIALIZERS);
        serializers.addAll(this.customSerializers);

        final var config = new Config<>(this.adapter, this.type, this.instance, this.file, serializers);
        config.load(true);

        return config;
    }

    private void ensureFileExists(final File file) {
        if (file.exists()) return;

        try {
            final var parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                if (!parent.mkdirs()) {
                    throw new IOException("Failed to create directories for " + parent.getPath());
                }
            }

            if (!file.createNewFile()) {
                throw new IOException("Failed to create config file " + file.getPath());
            }
        } catch (final IOException e) {
            throw new ConfigException("Cannot create config file: " + file.getPath(), e);
        }
    }
}
