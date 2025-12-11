package dev.kajteh.configma;

import dev.kajteh.configma.exception.ConfigException;
import dev.kajteh.configma.serialization.serializer.Serializer;
import dev.kajteh.configma.serialization.serializer.builtin.InstantSerializer;
import dev.kajteh.configma.serialization.serializer.builtin.UUIDSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ConfigBuilder<T> {

    private static final List<Serializer<?, ?>> BUILTIN_SERIALIZERS = List.of(
            new UUIDSerializer(),
            new InstantSerializer()
    );

    private final List<Serializer<?, ?>> serializers = new ArrayList<>(BUILTIN_SERIALIZERS);

    private final Class<T> type;

    private T instance;
    private Path path;

    ConfigBuilder(final Class<T> type) {
        this.type = type;
    }

    public ConfigBuilder<T> instance(final T instance) {
        this.instance = instance;
        return this;
    }

    public ConfigBuilder<T> path(@NotNull final Path path) {
        this.path = path;
        return this;
    }

    public ConfigBuilder<T> serializer(final Serializer<?, ?>... serializers) {
        this.serializers.addAll(List.of(serializers));
        return this;
    }

    public Config<T> load(@NotNull final ConfigLoader loader) {
        this.ensureFileExists();

        final var config = new Config<>(this.path, loader, type, this.instance, this.serializers);

        config.load(true);

        return config;
    }

    private void ensureFileExists() {
        try {
            final var parent = this.path.getParent();

            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            if (!Files.exists(this.path)) {
                Files.createFile(this.path);
            }
        } catch (final IOException e) {
            throw new ConfigException("Cannot create config file: " + this.path, e);
        }
    }
}
