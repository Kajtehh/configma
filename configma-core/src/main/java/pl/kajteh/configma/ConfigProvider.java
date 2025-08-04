package pl.kajteh.configma;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.kajteh.configma.annotation.Pathname;
import pl.kajteh.configma.annotation.ConfigIgnore;
import pl.kajteh.configma.exception.ConfigException;
import pl.kajteh.configma.exception.ConfigProcessingException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ConfigProvider<T> {

    private final T instance;
    private final File file;
    private final ConfigProcessor processor;
    private final YamlConfiguration configuration;
    private final List<ConfigExtension> extensions;

    public ConfigProvider(T instance, File file, ConfigProcessor processor, List<ConfigExtension> extensions) {
        this.instance = instance;
        this.file = file;
        this.processor = processor;
        this.extensions = extensions;

        this.configuration = YamlConfiguration.loadConfiguration(file);
        this.configuration.options().copyDefaults(true);
        this.extensions.forEach(extension ->
                extension.onLoad(this.instance.getClass(), this.configuration));
    }

    public void save(boolean toConfig) throws ConfigException {
        this.syncFields(toConfig);
        this.saveFile();
    }

    public void saveFile() {
        try {
            this.configuration.save(file);
        } catch (IOException e) {
            throw new ConfigException("Failed to save config file: " + file.getName(), e);
        }
    }

    public void reload() throws ConfigException {
        try {
            this.configuration.load(this.file);
            this.syncFields(false);
        } catch (Exception e) {
            throw new ConfigException("Failed to reload config file: " + file.getName(), e);
        }
    }

    private void syncFields(Class<?> clazz, Object currentInstance, String pathPrefix, boolean toConfig) throws ConfigException {
        for (Field field : Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(ConfigIgnore.class))
                .collect(Collectors.toList())) {
            field.setAccessible(true);

            try {
                final Object value = field.get(currentInstance);

                if (value == null) continue;

                final Pathname pathname = field.getAnnotation(Pathname.class);

                final String pathName = pathname != null && !pathname.value().isEmpty()
                        ? pathname.value()
                        : this.getFieldName(field);

                final String path = pathPrefix + pathName;

                if (value instanceof ConfigSection) {
                    this.syncFields(value.getClass(), value, path + this.configuration.options().pathSeparator(), toConfig);
                    continue;
                }

                if (toConfig) {
                    final Object finalValue = this.processor.process(field.getType(), value);

                    this.configuration.set(path, finalValue);
                    this.extensions.forEach(extension ->
                            extension.onFieldSaved(this.instance.getClass(), this.configuration, path, field, finalValue));
                    continue;
                }

                if (this.configuration.contains(path)) {
                    field.set(currentInstance, this.processor.processExisting(field.getType(), this.configuration.get(path)));
                    continue;
                }

                final Object finalValue = this.processor.process(field.getType(), value);

                this.configuration.set(path, finalValue);

                this.extensions.forEach(extension ->
                        extension.onFieldSaved(this.instance.getClass(), this.configuration, path, field, finalValue));
            } catch (IllegalAccessException e) {
                throw new ConfigException("Failed to access field: " + field.getName(), e);
            } catch (ConfigProcessingException e) {
                throw new ConfigException("Failed to process config for field: " + field.getName(), e);
            }
        }
    }

    private void syncFields(boolean toConfig) {
        this.syncFields(this.instance.getClass(), this.instance, "", toConfig);
    }

    public T getInstance() {
        return this.instance;
    }

    private String getFieldName(Field field) {
        // TODO: add more naming options, for now it's just kebab case
        return field.getName().replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }
}
