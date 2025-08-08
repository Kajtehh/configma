package pl.kajteh.configma.serialization.serializer;

import pl.kajteh.configma.serialization.data.SerializedData;
import pl.kajteh.configma.serialization.data.SerializationData;

public interface ObjectSerializer<T> extends Serializer<T> {
    void serialize(final SerializationData data, T t);
    T deserialize(final SerializedData data);
}
