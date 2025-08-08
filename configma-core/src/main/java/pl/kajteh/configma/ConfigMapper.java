package pl.kajteh.configma;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.kajteh.configma.annotation.IgnoreField;
import pl.kajteh.configma.annotation.IgnoreSerialization;
import pl.kajteh.configma.annotation.Pathname;
import pl.kajteh.configma.exception.ConfigException;
import pl.kajteh.configma.exception.ConfigProcessingException;
import pl.kajteh.configma.serialization.serializer.Serializer;
import pl.kajteh.configma.type.ConfigTypeCache;
import pl.kajteh.configma.type.ConfigTypeService;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ConfigMapper {

    private final YamlConfiguration configuration;
    private final List<ConfigExtension> extensions;
    private final ConfigFieldCache fieldCache;
    private final ConfigTypeCache typeCache;
    private final ConfigTypeService typeService;
    private final ConfigProcessor processor;

    ConfigMapper(YamlConfiguration configuration, List<Serializer<?>> serializers, List<ConfigExtension> extensions) {
        this.configuration = configuration;
        this.extensions = extensions;

        this.fieldCache = new ConfigFieldCache();
        this.typeCache = new ConfigTypeCache();
        this.typeService = new ConfigTypeService(typeCache);
        this.processor = new ConfigProcessor(typeCache, serializers);
    }

    public void syncFields(final Class<?> clazz, final Object instance, final String pathPrefix, final boolean toConfig) throws ConfigException {
        this.typeCache.clear();
        this.fieldCache.getFields(clazz).forEach(field -> {
            field.setAccessible(true);

            try {
                final Object value = field.get(instance);

                if (value == null) return;

                final Pathname pathname = field.getAnnotation(Pathname.class);
                final String pathName = pathname != null && !pathname.value().isEmpty()
                        ? pathname.value()
                        : this.getFieldName(field);

                final String path = pathPrefix + pathName;

                if (value instanceof ConfigSection) {
                    this.syncFields(value.getClass(), value, path + configuration.options().pathSeparator(), toConfig);
                    return;
                }

                this.typeService.loadTypes(path, field.getType(), value);

                final boolean ignoreSerialization = field.isAnnotationPresent(IgnoreSerialization.class);

                if (toConfig) {
                    final Object finalValue = ignoreSerialization ? value : processor.process(field.getType(), value);

                    configuration.set(path, finalValue);
                    this.callExtensions(clazz, path, field, finalValue);
                    return;
                }

                if (configuration.contains(path)) {
                    final Object configValue = configuration.get(path);

                    field.set(instance, ignoreSerialization ? configValue : processor.processExisting(path, configValue));
                    return;
                }

                final Object finalValue = ignoreSerialization ? value : processor.process(field.getType(), value);

                configuration.set(path, finalValue);
                this.callExtensions(clazz, path, field, finalValue);
            } catch (IllegalAccessException e) {
                throw new ConfigException("Failed to access field: " + field.getName(), e);
            } catch (ConfigProcessingException e) {
                throw new ConfigException("Failed to process config for field: " + field.getName(), e);
            }
        });
    }

    private void callExtensions(final Class<?> clazz, final String path, final Field field, final Object finalValue) {
        extensions.forEach(extension -> extension.onFieldSaved(clazz, configuration, path, field, finalValue));
    }

    private String getFieldName(final Field field) {
        // todo: add more naming options
        return field.getName().replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }
}
