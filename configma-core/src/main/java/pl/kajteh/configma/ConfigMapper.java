package pl.kajteh.configma;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.kajteh.configma.annotation.GenericTypes;
import pl.kajteh.configma.annotation.IgnoreSerialization;
import pl.kajteh.configma.annotation.Pathname;
import pl.kajteh.configma.exception.ConfigException;
import pl.kajteh.configma.exception.ConfigProcessingException;
import pl.kajteh.configma.serialization.serializer.Serializer;

import java.lang.reflect.Field;
import java.util.List;

public class ConfigMapper {

    private final YamlConfiguration configuration;
    private final List<ConfigExtension> extensions;
    private final ConfigFieldCache fieldCache;
    private final ConfigProcessor processor;

    ConfigMapper(final YamlConfiguration configuration, final List<Serializer<?>> serializers, final List<ConfigExtension> extensions) {
        this.configuration = configuration;
        this.extensions = extensions;

        this.fieldCache = new ConfigFieldCache();
        this.processor = new ConfigProcessor(serializers);
    }

    public void syncFields(final Class<?> clazz, final Object instance, final String pathPrefix, final boolean toConfig) throws ConfigException {
        this.fieldCache.getFields(clazz).forEach(field ->
                this.syncField(field, instance, pathPrefix, toConfig));
    }

    private void syncField(Field field, Object instance, String pathPrefix, boolean toConfig) throws ConfigException {
        try {
            final Object value = field.get(instance);

            if (value == null) return;

            final String path = this.buildPath(field, pathPrefix);

            if (value instanceof ConfigSection) {
                this.syncFields(value.getClass(), value, path + configuration.options().pathSeparator(), toConfig);
                return;
            }

            final boolean ignoreSerialization = field.isAnnotationPresent(IgnoreSerialization.class);

            if (toConfig) {
                final Object finalValue = ignoreSerialization ? value : processor.process(field.getType(), value); // todo tutaj blad

                this.configuration.set(path, finalValue);
                this.callExtensions(field.getDeclaringClass(), path, field, finalValue);
                return;
            }

            if (configuration.contains(path)) {
                final GenericTypes genericTypes = field.getAnnotation(GenericTypes.class);
                final Object configValue = configuration.get(path);

                field.set(instance, ignoreSerialization ? configValue : processor.processExisting(
                        field.getType(),
                        configValue,
                        (genericTypes == null || genericTypes.value().length == 0) ? null : List.of(genericTypes.value())
                ));
                return;
            }

            final Object finalValue = ignoreSerialization ? value : processor.process(field.getType(), value);

            this.configuration.set(path, finalValue);
            this.callExtensions(field.getDeclaringClass(), path, field, finalValue);
        } catch (IllegalAccessException | ConfigProcessingException e) {
            throw new ConfigException("Failed to sync field: " + field.getName(), e);
        }
    }

    private String buildPath(Field field, String pathPrefix) {
        final Pathname pathname = field.getAnnotation(Pathname.class);
        final String pathName = (pathname != null && !pathname.value().isEmpty())
                ? pathname.value()
                : this.getFieldName(field);

        return pathPrefix + pathName;
    }

    private void callExtensions(final Class<?> clazz, final String path, final Field field, final Object finalValue) {
        this.extensions.forEach(extension -> extension.onFieldSaved(clazz, this.configuration, path, field, finalValue));
    }

    private String getFieldName(final Field field) {
        // todo: add more naming options
        return field.getName().replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }
}
