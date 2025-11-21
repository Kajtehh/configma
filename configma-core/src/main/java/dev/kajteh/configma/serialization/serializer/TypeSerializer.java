package dev.kajteh.configma.serialization.serializer;

public interface TypeSerializer<T> extends Serializer<T> {
    Object serialize(final T t);
    T deserialize(final Object raw);
}