package dev.kajteh.configma.serialization.serializer;

public interface Serializer<T> {
    boolean matches(final Class<?> type);
}