package dev.kajteh.configma.serialization.converter;

import dev.kajteh.configma.serialization.SerializationService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import static dev.kajteh.configma.serialization.util.TypeUtil.typeArgument;

public final class CollectionConverter {

    private final SerializationService serializer;

    public CollectionConverter(final SerializationService serializer) {
        this.serializer = serializer;
    }

    public Collection<Object> serialize(
            final Collection<?> collection,
            final Type type
    ) {
        final var result = collection instanceof List<?>
                ? new ArrayList<>()
                : new LinkedHashSet<>();

        for (final var element : collection) {
            result.add(this.serializer.serializeValue(element, typeArgument(type, 0)));
        }

        return result;
    }

    public Collection<Object> deserialize(
            final Collection<?> collection,
            final Type type,
            final Class<?> rawType
    ) {
        final var result = List.class.isAssignableFrom(rawType)
                ? new ArrayList<>()
                : new LinkedHashSet<>();

        for (final var element : collection) {
            result.add(this.serializer.deserializeValue(element, typeArgument(type, 0)));
        }

        return result;
    }
}
