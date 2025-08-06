package pl.kajteh.configma;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.kajteh.configma.exception.ConfigException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class ConfigProvider<T> {

    private final File file;
    private final T instance;
    private final YamlConfiguration configuration;
    private final ConfigMapper synchronizer;

    public ConfigProvider(T instance, File file, ConfigProcessor processor, List<ConfigExtension> extensions) {
        this.instance = instance;
        this.file = file;

        this.configuration = YamlConfiguration.loadConfiguration(file);
        this.configuration.options().copyDefaults(true);

        this.synchronizer = new ConfigMapper(this.configuration, processor, extensions);

        extensions.forEach(extension ->
                extension.onLoad(this.instance.getClass(), this.configuration));
    }

    public void save(boolean toConfig) throws ConfigException {
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

    private void syncFields(boolean toConfig) {
        this.synchronizer.syncFields(this.instance.getClass(), this.instance, "", toConfig);
    }
}
