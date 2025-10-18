package dev.kajteh.configma.serialization.serializer;

import dev.kajteh.configma.serialization.SerializationContext;
import dev.kajteh.configma.serialization.DeserializationContext;

public interface ObjectSerializer<T> extends Serializer<T> {
    void serialize(final SerializationContext context, final T t);
    T deserialize(final DeserializationContext context);
}
