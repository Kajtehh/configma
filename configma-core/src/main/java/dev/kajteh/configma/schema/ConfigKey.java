package dev.kajteh.configma.schema;

import dev.kajteh.configma.annotation.Key;

import java.lang.reflect.Field;
import java.util.function.Function;

public record ConfigKey(String rawName, boolean exact) {

    public static ConfigKey of(final Field field) {
        final var fieldName = field.getName();
        final var annotation = field.getAnnotation(Key.class);

        final var keyName = annotation != null
                ? (annotation.value().isEmpty() ? fieldName : annotation.value())
                : fieldName;

        return new ConfigKey(keyName, annotation != null && annotation.exact());
    }

    public String name(final Function<String, String> formatter) {
        return this.exact ? this.rawName : formatter.apply(this.rawName);
    }
}
