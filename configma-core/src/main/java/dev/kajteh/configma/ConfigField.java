package dev.kajteh.configma;

import dev.kajteh.configma.annotation.Nested;
import dev.kajteh.configma.annotation.decoration.comment.Comment;
import dev.kajteh.configma.annotation.decoration.comment.InlineComment;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

public record ConfigField(Field rawField, String name, Type genericType, Class<?> type, List<String> comments, String inlineComment, boolean isNested) {

    public static ConfigField of(final Field field) {
        field.setAccessible(true);

        return new ConfigField(
                field,
                field.getName(),
                field.getGenericType(),
                field.getType(),
                field.isAnnotationPresent(Comment.class) ? List.of(field.getAnnotation(Comment.class).value()) : null,
                field.isAnnotationPresent(InlineComment.class) ? field.getAnnotation(InlineComment.class).value() : null,
                field.isAnnotationPresent(Nested.class)
        );
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

    public List<String> comments() {
        return this.comments;
    }

    public String inlineComment() {
        return this.inlineComment;
    }

    public ConfigSchema<?> nestedSchema(final Object parentInstance) {
        return ConfigSchema.ofNested(this, parentInstance);
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