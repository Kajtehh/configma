package dev.kajteh.configma.serialization.serializer;

public interface TypeSerializer<T, R> extends Serializer<T, R> {
    R serialize(final T t);
    T deserialize(final R r);
}