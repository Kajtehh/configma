package dev.kajteh.configma.serialization.serializer;

import dev.kajteh.configma.serialization.context.SerializationContext;
import dev.kajteh.configma.serialization.context.DeserializationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ObjectSerializer<T> extends Serializer<T, Map<String, Object>> {
    void serialize(@NotNull SerializationContext context, @NotNull T t);
    @Nullable T deserialize(@NotNull DeserializationContext context);
}
