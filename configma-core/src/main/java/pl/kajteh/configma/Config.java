
package pl.kajteh.configma;

import java.util.function.Consumer;
import java.util.function.Function;

public final class Config<T> {

    private final ConfigProvider<T> configProvider;

    public Config(final ConfigProvider<T> configProvider) {
        this.configProvider = configProvider;
        this.configProvider.save(false);
    }

    public void reload() {
        this.configProvider.reload();
    }

    public void save() {
        this.configProvider.save(true);
    }

    public Config<T> edit(final Consumer<T> consumer) {
        this.reload();
        consumer.accept(this.get());
        return this;
    }

    public Config<T> get(final Consumer<T> consumer) {
        consumer.accept(this.get());
        return this;
    }

    public T get() {
        return this.configProvider.instance;
    }

    public <R> R map(final Function<T, R> mapper) {
        return mapper.apply(this.get());
    }
}
