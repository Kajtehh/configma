package dev.kajteh.configma;

import dev.kajteh.configma.annotation.Nested;
import dev.kajteh.configma.annotation.decoration.comment.Comment;
import dev.kajteh.configma.annotation.decoration.comment.InlineComment;
import dev.kajteh.configma.exception.ConfigFieldException;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

public record ConfigField(Field rawField, ConfigKey key, Type genericType, Class<?> type, List<String> comments, String inlineComment, boolean isNested) {

    public static ConfigField of(final Field field) {
        field.setAccessible(true);

        return new ConfigField(
                field,
                ConfigKey.of(field),
                field.getGenericType(),
                field.getType(),
                field.isAnnotationPresent(Comment.class) ? List.of(field.getAnnotation(Comment.class).value()) : null,
                field.isAnnotationPresent(InlineComment.class) ? field.getAnnotation(InlineComment.class).value() : null,
                field.isAnnotationPresent(Nested.class)
        );
    }

    public ConfigSchema<?> nestedSchema(final Object parentInstance) {
        return ConfigSchema.ofNested(this, parentInstance);
    }

    public Object getValue(final Object instance) {
        try {
            return this.rawField.get(instance);
        } catch (final IllegalAccessException e) {
            throw new ConfigFieldException(
                    "Cannot access field '" + this.key.rawName() + "' of type " + this.rawField.getType().getSimpleName(), e
            );
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
            throw new ConfigFieldException(
                    "Failed to set value for field '" + this.key().rawName() +
                            "': expected type " + this.genericType() +
                            ", got " + (value != null ? value.getClass() : "null"), e
            );
        }
    }
}