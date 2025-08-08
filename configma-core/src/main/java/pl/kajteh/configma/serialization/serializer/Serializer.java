package pl.kajteh.configma.serialization.serializer;

public interface Serializer<T> {
    Class<T> getType();
}
