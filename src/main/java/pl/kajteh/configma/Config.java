package pl.kajteh.configma;

import org.bukkit.plugin.java.JavaPlugin;
import pl.kajteh.configma.exception.ConfigException;

import java.util.function.Consumer;
import java.util.function.Function;

public final class Config<T> {

    private final ConfigProvider<T> configProvider;

    public Config(ConfigProvider<T> configProvider) {
        this.configProvider = configProvider;
        this.configProvider.save(false);
    }

    public T get() {
        return this.configProvider.getInstance();
    }

    public T get(Consumer<T> instanceConsumer) {
        final T instance = this.get();

        instanceConsumer.accept(instance);

        return instance;
    }

    public <R> R get(Function<T, R> function) {
        return function.apply(this.get());
    }

    public void reload() throws ConfigException {
        this.configProvider.reload();
    }

    public void save() throws ConfigException {
        this.configProvider.save(true);
    }

    public void edit(Consumer<T> instanceConsumer) {
        this.edit(instanceConsumer, false);
    }

    public void edit(Consumer<T> instanceConsumer, boolean save) {
        this.reload();
        instanceConsumer.accept(this.get());

        if(save) this.save();
    }

    public static <T> ConfigBuilder<T> builder(JavaPlugin plugin, Class<T> clazz) {
        return new ConfigBuilder<>(plugin, clazz);
    }
}
