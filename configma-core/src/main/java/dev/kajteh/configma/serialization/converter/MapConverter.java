package dev.kajteh.configma.serialization.converter;

import dev.kajteh.configma.serialization.SerializationService;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import static dev.kajteh.configma.serialization.util.TypeUtil.typeArgument;

public final class MapConverter {

    private final SerializationService serializer;

    public MapConverter(final SerializationService serializer) {
        this.serializer = serializer;
    }

    public Map<Object, Object> serialize(
            final Map<?, ?> map,
            final Type type
    ) {
        final var result = new LinkedHashMap<>();

        final var keyType = typeArgument(type, 0);
        final var valueType = typeArgument(type, 1);

        for (final var entry : map.entrySet()) {
            result.put(
                    this.serializer.serializeValue(entry.getKey(), keyType),
                    this.serializer.serializeValue(entry.getValue(), valueType)
            );
        }

        return result;
    }

    public Map<Object, Object> deserialize(
            final Map<?, ?> map,
            final Type type
    ) {
        final var result = new LinkedHashMap<>();

        final var keyType = typeArgument(type, 0);
        final var valueType = typeArgument(type, 1);

        for (final var entry : map.entrySet()) {
            result.put(
                    this.serializer.deserializeValue(entry.getKey(), keyType),
                    this.serializer.deserializeValue(entry.getValue(), valueType)
            );
        }

        return result;
    }
}
