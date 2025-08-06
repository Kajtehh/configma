package pl.kajteh.configma;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.kajteh.configma.annotation.IgnoreField;
import pl.kajteh.configma.annotation.IgnoreSerialization;
import pl.kajteh.configma.annotation.Pathname;
import pl.kajteh.configma.exception.ConfigException;
import pl.kajteh.configma.exception.ConfigProcessingException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigMapper {

    private final YamlConfiguration configuration;
    private final ConfigProcessor processor;
    private final List<ConfigExtension> extensions;

    ConfigMapper(YamlConfiguration configuration, ConfigProcessor processor, List<ConfigExtension> extensions) {
        this.configuration = configuration;
        this.processor = processor;
        this.extensions = extensions;
    }

    public void syncFields(Class<?> clazz, Object instance, String pathPrefix, boolean toConfig) throws ConfigException {
        final List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(IgnoreField.class))
                .filter(field -> !Modifier.isFinal(field.getModifiers()))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .collect(Collectors.toList());

        fields.forEach(field -> {
            field.setAccessible(true);

            try {
                final Object value = field.get(instance);

                if (value == null) return;

                final Pathname pathname = field.getAnnotation(Pathname.class);
                final String pathName = pathname != null && !pathname.value().isEmpty()
                        ? pathname.value()
                        : getFieldName(field);

                final String path = pathPrefix + pathName;

                if (value instanceof ConfigSection) {
                    syncFields(value.getClass(), value, path + configuration.options().pathSeparator(), toConfig);
                    return;
                }

                final boolean ignoreSerialization = field.isAnnotationPresent(IgnoreSerialization.class);

                if (toConfig) {
                    final Object finalValue = ignoreSerialization ? value : processor.process(field.getType(), value);

                    configuration.set(path, finalValue);
                    callExtensions(clazz, path, field, finalValue);
                    return;
                }

                if (configuration.contains(path)) {
                    final Object configValue = configuration.get(path);

                    field.set(instance, ignoreSerialization ? configValue : processor.processExisting(field.getType(), configValue));
                    return;
                }

                final Object finalValue = ignoreSerialization ? value : processor.process(field.getType(), value);

                configuration.set(path, finalValue);
                callExtensions(clazz, path, field, finalValue);
            } catch (IllegalAccessException e) {
                throw new ConfigException("Failed to access field: " + field.getName(), e);
            } catch (ConfigProcessingException e) {
                throw new ConfigException("Failed to process config for field: " + field.getName(), e);
            }
        });
    }

    private void callExtensions(Class<?> clazz, String path, Field field, Object finalValue) {
        extensions.forEach(extension -> extension.onFieldSaved(clazz, configuration, path, field, finalValue));
    }

    private String getFieldName(Field field) {
        // todo: add more naming options
        return field.getName().replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }
}
