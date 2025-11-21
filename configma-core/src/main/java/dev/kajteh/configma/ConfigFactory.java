package dev.kajteh.configma;

import java.util.function.Consumer;

public final class ConfigFactory {

    private ConfigFactory() {}

    public static <T> ConfigBuilder<T> builder(final Class<T> type) {
        return new ConfigBuilder<>(type);
    }

    public static <T> Config<T> create(final Class<T> type, final Consumer<ConfigBuilder<T>> builderConsumer) {
        final var builder = builder(type);

        builderConsumer.accept(builder);

        return builder.build();
    }
}
