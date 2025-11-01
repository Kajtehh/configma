package dev.kajteh.configma;

import java.util.function.Consumer;

public final class ConfigFactory {

    private ConfigFactory() {}

    public static <T> ConfigBuilder<T> builder(final Class<T> type) {
        return new ConfigBuilder<>(type);
    }

    public static <T> ConfigBuilder<T> builder(final Class<T> type, final T instance) {
        return new ConfigBuilder<>(type, instance);
    }

    public static <T> Config<T> create(final Class<T> type, final Consumer<ConfigBuilder<T>> builderConsumer) {
        return buildConfig(builder(type), builderConsumer);
    }

    public static <T> Config<T> create(final Class<T> type, final T instance, final Consumer<ConfigBuilder<T>> builderConsumer) {
        return buildConfig(builder(type, instance), builderConsumer);
    }

    private static <T> Config<T> buildConfig(final ConfigBuilder<T> builder, final Consumer<ConfigBuilder<T>> consumer) {
        consumer.accept(builder);
        return builder.build();
    }
}
