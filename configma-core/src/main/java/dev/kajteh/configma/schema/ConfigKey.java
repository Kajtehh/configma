package dev.kajteh.configma.schema;

import dev.kajteh.configma.annotation.Key;

import java.lang.reflect.Field;
import java.util.function.Function;

public record ConfigKey(String rawName, boolean exact) {

    public static ConfigKey of(final Field field) {
        final var fieldName = field.getName();
        final var key = field.getAnnotation(Key.class);

        final var keyName = key != null
                ? (key.value().isEmpty() ? fieldName : key.value())
                : fieldName;

        return new ConfigKey(
                keyName,
                key != null && key.exact()
        );
    }

    public String name(final Function<String, String> formatter) {
        return this.exact ? this.rawName : formatter.apply(this.rawName);
    }
}
