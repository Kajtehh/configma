package pl.kajteh.configma;

import pl.kajteh.configma.annotation.IgnoreField;
import pl.kajteh.configma.annotation.Section;
import pl.kajteh.configma.serialization.serializer.Serializer;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ConfigMapper {

    private final ConfigProcessor configProcessor;
    protected Map<String, Object> values = new LinkedHashMap<>();

    public ConfigMapper(final List<Serializer> serializers) {
        this.configProcessor = new ConfigProcessor(serializers);
    }

    public void syncFields(final Class<?> clazz, final Object instance, final boolean writeMode) {
        for (final Field field : this.getFields(clazz.getDeclaredFields())) {
            this.syncField(field, instance, writeMode);
        }
    }

    private void syncField(final Field field, final Object instance, boolean writeMode) throws ConfigException {
        try {
            final Object currentValue = field.get(instance);

            if(currentValue == null) return;

            if(field.isAnnotationPresent(Section.class)) {
                this.syncFields(currentValue.getClass(), currentValue, writeMode);
                return;
            }

            final String name = field.getName();
            final Class<?> type = field.getType();

            this.values.put(name, writeMode
                    ? this.configProcessor.process(type, currentValue)
                    : this.values.containsKey(name)
                        ? this.configProcessor.processExisting(type, currentValue)
                        : this.configProcessor.process(type, currentValue));
        } catch (final IllegalAccessException e) {
            throw new ConfigException(e);
        }
    }

    private List<Field> getFields(final Field[] fields) {
        return Arrays.stream(fields)
                .filter(field -> !field.isAnnotationPresent(IgnoreField.class))
                .filter(field -> !Modifier.isFinal(field.getModifiers()))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .toList();
    }

    protected abstract void load(final File file);
    protected abstract void write(final File file);
}
