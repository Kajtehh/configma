package dev.kajteh.configma;

public final class ConfigFactory {

    private ConfigFactory() {}

    public static <T> ConfigBuilder<T> builder(final Class<T> type) {
        return new ConfigBuilder<>(type);
    }

    public static <T> ConfigBuilder<T> builder(final Class<T> type, final T instance) {
        return new ConfigBuilder<>(type, instance);
    }
}
