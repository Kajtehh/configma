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
        this.configProvider.reload();
    }

    public void save() throws ConfigException {
        this.configProvider.save(true);
    }

    public Config<T> edit(final Consumer<T> consumer) throws ConfigException {
        this.reload();
        consumer.accept(this.get());
        return this;
    }

    public Config<T> get(final Consumer<T> consumer) {
        consumer.accept(this.get());
        return this;
    }

    public T get() {
        return this.configProvider.getInstance();
    }

    public <R> R map(final Function<T, R> mapper) {
        return mapper.apply(this.get());
    }

    public static <T> ConfigBuilder<T> builder(final JavaPlugin plugin, final Class<T> clazz) {
        return new ConfigBuilder<>(plugin, clazz);
    }
}
