package dev.kajteh.configma.serialization.serializer;

import org.jetbrains.annotations.NotNull;

public interface Serializer<T, R> {
    boolean matches(@NotNull Class<?> type);
}