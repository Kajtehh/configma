package pl.kajteh.configma;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.kajteh.configma.exception.ConfigException;
import pl.kajteh.configma.serialization.serializer.Serializer;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class ConfigProvider<T> {

    private final File file;
    private final T instance;
    private final YamlConfiguration configuration;
    private final ConfigMapper configMapper;

    ConfigProvider(final T instance, final File file, final List<Serializer<?>> serializers, final List<ConfigExtension> extensions) {
        this.instance = instance;
        this.file = file;

        this.configuration = YamlConfiguration.loadConfiguration(file);
        this.configuration.options().copyDefaults(true);

        this.configMapper = new ConfigMapper(this.configuration, serializers, extensions);

        extensions.forEach(extension ->
                extension.onLoad(this.instance.getClass(), this.configuration));
    }

    public void save(final boolean toConfig) throws ConfigException {
        this.syncFields(toConfig);

        try {
            this.configuration.save(file);
        } catch (IOException e) {
            throw new ConfigException("Failed to save config file: " + file.getName(), e);
        }
    }

    public T getInstance() {
        return this.instance;
    }

    public void reload() throws ConfigException {
        try {
            this.configuration.load(this.file);
            this.syncFields(false);
        } catch (Exception e) {
            throw new ConfigException("Failed to reload config file: " + file.getName(), e);
        }
    }

    private void syncFields(final boolean toConfig) {
        this.configMapper.syncFields(this.instance.getClass(), this.instance, "", toConfig);
    }
}
