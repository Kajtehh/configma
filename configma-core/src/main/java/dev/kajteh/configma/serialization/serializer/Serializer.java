package dev.kajteh.configma.serialization.serializer;

public interface Serializer<T, R> {
    boolean matches(final Class<?> type);
}