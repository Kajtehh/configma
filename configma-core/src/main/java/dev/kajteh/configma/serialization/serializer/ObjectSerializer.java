package dev.kajteh.configma.serialization.serializer;

import dev.kajteh.configma.serialization.SerializationContext;
import dev.kajteh.configma.serialization.DeserializationContext;

import java.util.Map;

public interface ObjectSerializer<T> extends Serializer<T, Map<String, Object>> {
    void serialize(final SerializationContext context, final T t);
    T deserialize(final DeserializationContext context);
}
