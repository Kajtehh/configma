package dev.kajteh.configma;

import dev.kajteh.configma.annotation.Key;

import java.lang.reflect.Field;

public record ConfigKey(String rawName, boolean exact) {

    public static ConfigKey of(final Field field) {
        final var fieldName = field.getName();
        final var annotation = field.getAnnotation(Key.class);

        final var keyName = annotation != null
                ? (annotation.value().isEmpty() ? fieldName : annotation.value())
                : fieldName;

        return new ConfigKey(keyName, annotation != null && annotation.exact());
    }

    public String name(final ConfigParser parser) {
        return this.exact ? this.rawName : parser.formatField(this.rawName);
    }
}
