package dev.kajteh.configma.schema;

import dev.kajteh.configma.ConfigLoader;
import dev.kajteh.configma.annotation.Key;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

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

    public String name(final @NotNull ConfigLoader loader) {
        return this.exact ? this.rawName : loader.formatField(this.rawName);
    }
}
