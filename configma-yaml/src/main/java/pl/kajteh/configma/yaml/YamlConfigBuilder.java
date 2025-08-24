package pl.kajteh.configma.yaml;

import pl.kajteh.configma.ConfigBuilder;
import pl.kajteh.configma.ConfigProvider;
import pl.kajteh.configma.serialization.serializer.Serializer;

import java.io.File;
import java.util.List;

public final class YamlConfigBuilder<T> extends ConfigBuilder<T> {

    YamlConfigBuilder(final Class<T> configClass) {
        super(configClass);
    }

    YamlConfigBuilder(final T instance) {
        super(instance);
    }

    @Override
    protected ConfigProvider<T> createProvider(final T instance, final File configFile, final List<Serializer> serializers) {
        return new YamlConfigProvider<>(instance, configFile, serializers);
    }
}
