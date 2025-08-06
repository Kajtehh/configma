package pl.kajteh.configma;

import org.bukkit.plugin.java.JavaPlugin;
import pl.kajteh.configma.exception.ConfigException;

import java.util.function.Consumer;
import java.util.function.Function;

public final class Config<T> {

    private final ConfigProvider<T> configProvider;

    Config(ConfigProvider<T> configProvider) {
        this.configProvider = configProvider;
        this.configProvider.save(false);
    }

    public void reload() throws ConfigException {
        configProvider.reload();
    }

    public void save() throws ConfigException {
        configProvider.save(true);
    }

    public Config<T> edit(Consumer<T> consumer) throws ConfigException {
        reload();
        consumer.accept(get());
        return this;
    }

    public Config<T> get(Consumer<T> consumer) {
        consumer.accept(get());
        return this;
    }

    public T get() {
        return configProvider.getInstance();
    }

    public <R> R map(Function<T, R> mapper) {
        return mapper.apply(get());
    }

    public static <T> ConfigBuilder<T> builder(JavaPlugin plugin, Class<T> clazz) {
        return new ConfigBuilder<>(plugin, clazz);
    }
}
