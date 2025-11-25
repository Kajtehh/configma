package dev.kajteh.configma;

import dev.kajteh.configma.exception.ConfigException;
import dev.kajteh.configma.serialization.serializer.Serializer;
import dev.kajteh.configma.serialization.serializer.builtin.InstantSerializer;
import dev.kajteh.configma.serialization.serializer.builtin.UUIDSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ConfigBuilder<T> {

    private static final List<Serializer<?, ?>> BUILTIN_SERIALIZERS = List.of(
            new UUIDSerializer(),
            new InstantSerializer()
    );

    private final Class<T> type;
    private final List<Serializer<?, ?>> additionalSerializers = new ArrayList<>();

    private T instance;
    private ConfigParser parser;
    private File file;
    //private boolean removeOrphans; TODO: 11/24/2025
    private boolean autoLoad = true;

    public ConfigBuilder(final Class<T> type) {
        this.type = type;
    }

    public ConfigBuilder<T> instance(final T instance) {
        this.instance = instance;
        return this;
    }

    public ConfigBuilder<T> file(final File file) {
        this.file = file;
        return this;
    }

    public ConfigBuilder<T> file(final Path path) {
        return this.file(path.toFile());
    }

    public ConfigBuilder<T> file(final String pathname) {
        return this.file(new File(pathname));
    }

    public ConfigBuilder<T> format(final ConfigParser parser) {
        this.parser = parser;
        return this;
    }

    public ConfigBuilder<T> serializer(final Serializer<?, ?>... serializers) {
        this.additionalSerializers.addAll(List.of(serializers));
        return this;
    }

//    public ConfigBuilder<T> removeOrphans(final boolean removeOrphans) {
//        this.removeOrphans = removeOrphans;
//        return this;
//    }

    public ConfigBuilder<T> autoLoad(final boolean autoLoad) {
        this.autoLoad = autoLoad;
        return this;
    }

    public Config<T> build() {
        if (this.file == null)
            throw new ConfigException("Config file cannot be null");
        if (this.parser == null)
            throw new ConfigException("Config parser cannot be null");

        this.ensureFileExists(file);

        final var serializers = new ArrayList<>(BUILTIN_SERIALIZERS);
        serializers.addAll(this.additionalSerializers);

        final var config = new Config<>(this.file, this.parser, this.type, this.instance, serializers);

        if (autoLoad) config.load(true);

        return config;
    }

    private void ensureFileExists(final File file) {
        try {
            final var parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IOException("Failed to create directories for " + parent.getPath());
            }

            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Failed to create config file " + file.getPath());
            }
        } catch (final IOException e) {
            throw new ConfigException("Cannot create config file: " + file.getPath(), e);
        }
    }
}
