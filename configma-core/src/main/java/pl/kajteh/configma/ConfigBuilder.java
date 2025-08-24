package pl.kajteh.configma;

import pl.kajteh.configma.serialization.serializer.Serializer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ConfigBuilder<T> {
    private final T instance;
    private final List<Serializer> serializers = new ArrayList<>();

    private File configFile;

    protected ConfigBuilder(final T instance) {
        this.instance = instance;
    }

    protected ConfigBuilder(final Class<T> configClass) {
        final T instance;
        try {
            instance = configClass.getDeclaredConstructor().newInstance();
        } catch (final Exception e) {
            throw new ConfigException("Failed to instantiate config class " + configClass.getName(), e);
        }

        this.instance = instance;
    }

    public ConfigBuilder<T> file(final File configFile) {
        this.configFile = configFile;
        return this;
    }

    public ConfigBuilder<T> serializers(final Serializer... serializers) {
        this.serializers.addAll(Arrays.asList(serializers));
        return this;
    }

    protected abstract ConfigProvider<T> createProvider(final T instance, final File configFile, final List<Serializer> serializers);

    public Config<T> load() {
        if (this.configFile == null) {
            throw new ConfigException("Config file cannot be null");
        }

        final ConfigProvider<T> provider = this.createProvider(this.instance, this.configFile, this.serializers);
        return new Config<>(provider);
    }
}