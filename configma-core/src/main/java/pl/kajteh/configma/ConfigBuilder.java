package pl.kajteh.configma;

import org.bukkit.plugin.java.JavaPlugin;
import pl.kajteh.configma.exception.ConfigException;
import pl.kajteh.configma.serialization.serializer.Serializer;
import pl.kajteh.configma.serialization.serializer.SerializerPack;
import pl.kajteh.configma.serialization.serializer.impl.InstantSerializer;
import pl.kajteh.configma.serialization.serializer.impl.UUIDSerializer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class ConfigBuilder<T> {
    private final JavaPlugin plugin;
    private final Class<T> clazz;

    private final List<ConfigExtension> extensions = new ArrayList<>();
    private final List<Serializer<?>> serializers = new ArrayList<>();

    private T instance;
    private File file;

    ConfigBuilder(JavaPlugin plugin, Class<T> clazz) {
        this.plugin = plugin;
        this.clazz = clazz;

        this.serializers.addAll(List.of(new InstantSerializer(), new UUIDSerializer()));
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

    public ConfigBuilder<T> serializers(Serializer<?>... serializers) {
        this.serializers.addAll(List.of(serializers));
        return this;
    }

    public ConfigBuilder<T> serializerPacks(SerializerPack... packs) {
        for (SerializerPack pack : packs) {
            this.serializers.addAll(pack.getSerializers());
        }
        return this;
    }

    public ConfigBuilder<T> extensions(ConfigExtension... extensions) {
        this.extensions.addAll(List.of(extensions));
        return this;
    }

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

        final ConfigProvider<T> configProvider = new ConfigProvider<>(this.instance, this.file, this.serializers, this.extensions);

        return new Config<>(configProvider);
    }
}
