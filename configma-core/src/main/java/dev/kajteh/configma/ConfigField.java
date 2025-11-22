package dev.kajteh.configma;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

public final class ConfigField {

    private final Field rawField;
    private final String name;
    private final Type genericType;
    private final Class<?> type;
    private final List<String> comments;
    private final String inlineComment;
    private final boolean nested;
    private final ConfigSchema<?> nestedSchema;

    public ConfigField(final Field field, final String name, final Type genericType, final Class<?> type, final List<String> comments, String inlineComment, final boolean nested) {
        this.rawField = field;
        this.name = name;
        this.genericType = genericType;
        this.type = type;
        this.comments = comments;
        this.inlineComment = inlineComment;
        this.nested = nested;
        this.nestedSchema = nested ? new ConfigSchema<>(type) : null;

        field.setAccessible(true);
    }

    public String name() {
        return this.name;
    }

    public Type genericType() {
        return this.genericType;
    }

    public Class<?> type() {
        return this.type;
    }

    public boolean isNestedConfig() {
        return this.nested;
    }

    public ConfigSchema<?> nestedSchema() {
        return this.nestedSchema;
    }

    public List<String> comments() {
        return this.comments;
    }

    public String inlineComment() {
        return this.inlineComment;
    }

    public Object getValue(final Object instance) {
        try {
            return this.rawField.get(instance);
        } catch (final IllegalAccessException e) {
            throw new ConfigException("Cannot read field " + name, e);
        }
    }

    public void setValue(final Object instance, Object value) {
        try {
            if (value instanceof Collection<?> collection) {
                value = collection instanceof List<?> ? new ArrayList<>(collection)
                        : new LinkedHashSet<>(collection);
            } else if (value instanceof Map<?, ?> map) {
                value = new LinkedHashMap<>(map);
            }

            this.rawField.set(instance, value);
        } catch (final IllegalAccessException | IllegalArgumentException e) {
            throw new ConfigException("Cannot set field " + name, e);
        }
    }
}