package pl.kajteh.configma.serialization.serializer;

import pl.kajteh.configma.serialization.SerializationData;
import pl.kajteh.configma.serialization.SerializedData;

public interface ObjectSerializer<T> extends Serializer {
    void serialize(final SerializationData data, T t);
    T deserialize(final SerializedData data);
}
