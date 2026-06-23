package dev.kajteh.configma;

import dev.kajteh.configma.exception.ConfigException;
import dev.kajteh.configma.serialization.serializer.Serializer;
import dev.kajteh.configma.serialization.serializer.builtin.InstantSerializer;
import dev.kajteh.configma.serialization.serializer.builtin.RecordSerializer;
import dev.kajteh.configma.serialization.serializer.builtin.UUIDSerializer;
import dev.kajteh.configma.serialization.util.TypeUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

import static dev.kajteh.configma.serialization.util.TypeUtil.createInstance;

public final class ConfigBuilder<T> {

    private static final List<Serializer<?, ?>> BUILTIN_SERIALIZERS = List.of(
            new UUIDSerializer(),
            new InstantSerializer()
            //new RecordSerializer()
    );

    private final List<Serializer<?, ?>> serializers = new ArrayList<>(BUILTIN_SERIALIZERS);

    private final Class<T> type;

    private T instance;
    private Path path;

    ConfigBuilder(final @NotNull Class<T> type) {
        this.type = type;
    }

    public ConfigBuilder<T> instance(final @NotNull T instance) {
        this.instance = instance;
        return this;
    }

    public ConfigBuilder<T> path(final @NotNull Path path) {
        this.path = path;
        return this;
    }

    public ConfigBuilder<T> serializer(final Serializer<?, ?>... serializers) {
        this.serializers.addAll(List.of(serializers));
        return this;
    }

    public Config<T> load(final @NotNull ConfigLoader loader) {
        final Path finalPath = this.ensureFileExists(loader);

        final var config = new Config<>(finalPath, loader, type, this.instance, this.serializers);

        config.load(true);

        return config;
    }

    private @NotNull Path ensureFileExists(final @NotNull ConfigLoader loader) {
        final String extension = "." + loader.fileExtension();

        final Path finalPath = this.path.toString().endsWith(extension)
                ? this.path
                : this.path.resolveSibling(this.path.getFileName().toString() + extension);

        try {
            final var parent = finalPath.getParent();

            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            if (!Files.exists(finalPath)) {
                Files.createFile(finalPath);
            }

            return finalPath;
        } catch (final IOException e) {
            throw new ConfigException("Cannot create config file: " + finalPath, e);
        }
    }
}
