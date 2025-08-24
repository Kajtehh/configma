package pl.kajteh.configma.yaml;

public final class YamlConfig {

    public static <T> YamlConfigBuilder<T> builder(final Class<T> configClass) {
        return new YamlConfigBuilder<>(configClass);
    }

    public static <T> YamlConfigBuilder<T> builder(final T instance) {
        return new YamlConfigBuilder<>(instance);
    }
}
