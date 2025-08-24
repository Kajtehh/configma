package pl.kajteh.configma.serialization.serializer;

public interface Serializer {
    boolean matches(final Class<?> type);
}