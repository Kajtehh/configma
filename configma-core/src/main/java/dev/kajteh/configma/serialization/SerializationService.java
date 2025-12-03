package dev.kajteh.configma.serialization;

import dev.kajteh.configma.exception.ConfigException;
import dev.kajteh.configma.serialization.context.DeserializationContext;
import dev.kajteh.configma.serialization.context.SerializationContext;
import dev.kajteh.configma.serialization.serializer.Serializer;
import dev.kajteh.configma.serialization.util.TypeUtil;
import dev.kajteh.configma.serialization.converter.CollectionConverter;
import dev.kajteh.configma.serialization.converter.MapConverter;
import dev.kajteh.configma.serialization.serializer.ObjectSerializer;
import dev.kajteh.configma.serialization.serializer.TypeSerializer;

import java.lang.reflect.Type;
import java.util.*;

import static dev.kajteh.configma.serialization.converter.PrimitiveConverter.*;
import static dev.kajteh.configma.serialization.util.TypeUtil.*;

public final class SerializationService {

    private final SerializerRegistry serializerRegistry;

    private final MapConverter mapConverter = new MapConverter(this);
    private final CollectionConverter collectionConverter = new CollectionConverter(this);

    public SerializationService(final List<Serializer<?, ?>> serializers) {
        this.serializerRegistry = new SerializerRegistry(serializers);
    }

    public <T> Object serializeValue(
            final T value,
            final Type type
    ) {
        if (value == null) return null;

        final var serializer = this.serializerRegistry.findSerializer(TypeUtil.rawType(type));

        if(serializer != null) {
            return switch (serializer) {
                case TypeSerializer<?, ?> typeSerializer -> asTypeSerializer(typeSerializer).serialize(value);

                case ObjectSerializer<?> objectSerializer -> {
                    final var context = new SerializationContext(this);
                    asObjectSerializer(objectSerializer).serialize(context, value);

                    yield context.values();
                }

                default -> throw new ConfigException("Unsupported serializer type: " + serializer.getClass());
            };
        }

        return switch (value) {
            case Enum<?> e -> e.name();

            case Collection<?> collection -> this.collectionConverter.serialize(collection, type);

            case Map<?, ?> map -> this.mapConverter.serialize(map, type);

            default -> value;
        };
    }

    @SuppressWarnings("unchecked")
    public <T> T deserializeValue(
            final Object raw,
            final Type type
    ) {
        if (raw == null) return null;

        final var rawType = TypeUtil.rawType(type);

        if (rawType.isEnum() && raw instanceof String s) {
            return (T) Enum.valueOf(rawType.asSubclass(Enum.class), s);
        }

        final var serializer = this.serializerRegistry.findSerializer(rawType);

        if(serializer != null) {
            return (T) switch (serializer) {
                case TypeSerializer<?, ?> typeSerializer -> asTypeSerializer(typeSerializer).deserialize(raw);

                case ObjectSerializer<?> objectSerializer when raw instanceof Map<?, ?> map ->
                        asObjectSerializer(objectSerializer)
                                .deserialize(new DeserializationContext(this, (Map<String, Object>) map));

                default -> throw new ConfigException("Unsupported serializer type: " + serializer.getClass());
            };
        }

        return (T) switch (raw) {
            case Collection<?> collection -> this.collectionConverter.deserialize(collection, type, rawType);
            case Map<?, ?> map -> this.mapConverter.deserialize(map, type);

            case Number number -> convertNumber(number, rawType);
            case String s -> convertString(s, rawType);

            default -> raw;
        };
    }
}
