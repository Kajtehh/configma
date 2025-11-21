package dev.kajteh.configma;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public final class ConfigField {

    private final Field rawField;
    private final String name;
    private final Type genericType;
    private final Class<?> type;
    private final String[] comment;
    private final boolean nested;
    private final ConfigSchema<?> nestedSchema;

    public ConfigField(final Field field, final String name, final Type genericType, final Class<?> type, final String[] comment, final boolean nested) {
        this.rawField = field;
        this.name = name;
        this.genericType = genericType;
        this.type = type;
        this.comment = comment;
        this.nested = nested;

        field.setAccessible(true);

        this.nestedSchema = nested ? new ConfigSchema<>(type) : null;
    }

    public String name() {
        return name;
    }

    public Type genericType() {
        return genericType;
    }

    public Class<?> type() {
        return type;
    }

    public boolean isNestedConfig() {
        return nested;
    }

    public ConfigSchema<?> nestedSchema() {
        return nestedSchema;
    }

    public String[] comment() {
        return this.comment;
    }

    public Object getValue(final Object instance) {
        try {
            return this.rawField.get(instance);
        } catch (final Exception e) {
            throw new ConfigException("Cannot read field " + name, e);
        }
    }

    public void setValue(final Object instance, final Object value) {
        try {
            this.rawField.set(instance, value);
        } catch (final Exception e) {
            throw new ConfigException("Cannot set field " + name, e);
        }
    }
}