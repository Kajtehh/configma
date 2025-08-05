package pl.kajteh.configma;

import org.bukkit.plugin.java.JavaPlugin;
import pl.kajteh.configma.exception.ConfigException;
import pl.kajteh.configma.serializer.ConfigSerializer;
import pl.kajteh.configma.serializer.standard.EnumSerializer;
import pl.kajteh.configma.serializer.standard.InstantSerializer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class ConfigBuilder<T> {
    private final JavaPlugin plugin;
    private final Class<T> clazz;
    private File file;
    private T instance;
    private List<ConfigExtension> extensions = new ArrayList<>();
    private List<ConfigSerializer<?>> serializers = new ArrayList<>();

    public ConfigBuilder(JavaPlugin plugin, Class<T> clazz) {
        this.plugin = plugin;
        this.clazz = clazz;
        this.serializers.addAll(List.of(new EnumSerializer<>(), new InstantSerializer()));
    }

    public ConfigBuilder<T> file(File file) {
        this.file = file;
        return this;
    }

    public ConfigBuilder<T> file(String name) {
        this.file = new File(this.plugin.getDataFolder(), name);
        return this;
    }

    public ConfigBuilder<T> instance(T instance) {
        this.instance = instance;
        return this;
    }

    public ConfigBuilder<T> serializers(ConfigSerializer<?>... serializers) {
        this.serializers.addAll(List.of(serializers));
        return this;
    }

    public ConfigBuilder<T> extensions(ConfigExtension... extensions) {
        this.extensions.addAll(List.of(extensions));
        return this;
    }

    /* TODO: add debug
    public ConfigBuilder<T> enableDebug() {
        this.debug = true;
        return this;
    }*/

    public Config<T> build() {
        if(this.file == null) {
            throw new ConfigException("Config file cannot be null");
        }

        if(this.instance == null) {
            try {
                this.instance = this.clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new ConfigException("Failed to instantiate config class " + this.clazz.getName(), e);
            }
        }

        final ConfigProcessor processor = new ConfigProcessor(this.serializers);
        final ConfigProvider<T> configProvider = new ConfigProvider<>(this.instance, this.file, processor, this.extensions);

        return new Config<>(configProvider);
    }
}
