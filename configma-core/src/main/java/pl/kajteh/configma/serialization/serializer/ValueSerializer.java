package pl.kajteh.configma.serialization.serializer;

public interface ValueSerializer<T> extends Serializer {
    Object serialize(final T t);
    T deserialize(final Object raw);
}