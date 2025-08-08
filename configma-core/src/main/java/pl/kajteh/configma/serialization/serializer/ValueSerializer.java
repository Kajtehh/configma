package pl.kajteh.configma.serialization.serializer;

public interface ValueSerializer<T> extends Serializer<T> {
    Object serialize(T t);
    T deserialize(Object raw);
}
