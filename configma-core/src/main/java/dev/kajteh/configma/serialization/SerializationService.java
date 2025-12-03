package dev.kajteh.configma.serialization;

import dev.kajteh.configma.exception.ConfigException;
import dev.kajteh.configma.serialization.helper.PrimitiveConverter;
import dev.kajteh.configma.serialization.helper.TypeUtil;
import dev.kajteh.configma.serialization.serializer.ObjectSerializer;
import dev.kajteh.configma.serialization.serializer.Serializer;
import dev.kajteh.configma.serialization.serializer.TypeSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public final class SerializationService {

    private final SerializerRegistry serializerRegistry;

    public SerializationService(final SerializerRegistry serializerRegistry) {
        this.serializerRegistry = serializerRegistry;
    }

    public <T> Object serializeValue(final T value, final Type type) {
        if (value == null) return null;

        final var serializer = this.serializerRegistry.findSerializer(TypeUtil.rawType(type));

        if(serializer != null)
            return switch (serializer) {
                case TypeSerializer<?, ?> typeSerializer -> TypeUtil.asTypeSerializer(typeSerializer).serialize(value);
                case ObjectSerializer<?> objectSerializer -> {
                    final var context = new SerializationContext(this);

                    TypeUtil.asObjectSerializer(objectSerializer).serialize(context, value);

                    yield context.values();
                }
                default -> throw new ConfigException("Unsupported serializer type: " + serializer.getClass());
            };

        return switch (value) {
            case Enum<?> e -> e.name();
            case Collection<?> collection -> this.serializeCollection(collection, TypeUtil.typeArgument(type, 0));
            case Map<?, ?> map -> this.serializeMap(map, TypeUtil.typeArgument(type, 0), TypeUtil.typeArgument(type, 1));
            default -> value;
        };
    }

    @SuppressWarnings("unchecked")
    public <T> T deserializeValue(final Object raw, final Type type) {
        if (raw == null) return null;

        final var rawType = TypeUtil.rawType(type);

        if (rawType.isEnum() && raw instanceof String s) {
            return (T) Enum.valueOf(rawType.asSubclass(Enum.class), s);
        }

        final var serializer = this.serializerRegistry.findSerializer(rawType);

        if(serializer != null)
            return (T) switch (serializer) {
                case TypeSerializer<?, ?> typeSerializer -> TypeUtil.asTypeSerializer(typeSerializer).deserialize(raw);
                case ObjectSerializer<?> objectSerializer when raw instanceof Map<?, ?> map -> TypeUtil.asObjectSerializer(objectSerializer).deserialize(
                        new DeserializationContext(this, (Map<String, Object>) map)
                );
                default -> throw new ConfigException("Unsupported serializer type: " + serializer.getClass());
            };

        return (T) switch (raw) {
            case Collection<?> collection -> this.deserializeCollection(collection, TypeUtil.typeArgument(type, 0), rawType);
            case Map<?, ?> map -> this.deserializeMap(map, TypeUtil.typeArgument(type, 0), TypeUtil.typeArgument(type, 1));
            case Number number -> PrimitiveConverter.convertNumber(number, rawType);
            case String s -> PrimitiveConverter.convertString(s, rawType);
            default -> raw;
        };
    }

    private Collection<Object> serializeCollection(final Collection<?> collection, final Type elementType) {
        final var result = collection instanceof List ? new ArrayList<>() : new LinkedHashSet<>();
        for (final var element : collection) {
            result.add(this.serializeValue(element, elementType));
        }
        return result;
    }

    private Map<Object, Object> serializeMap(final Map<?, ?> map, final Type keyType, final Type valueType) {
        final var result = new LinkedHashMap<>();
        for (final var entry : map.entrySet()) {
            result.put(
                    this.serializeValue(entry.getKey(), keyType),
                    this.serializeValue(entry.getValue(), valueType)
            );
        }
        return result;
    }

    private Collection<Object> deserializeCollection(final Collection<?> collection, final Type elementType, final Class<?> rawType) {
        final var result = List.class.isAssignableFrom(rawType) ? new ArrayList<>() : new LinkedHashSet<>();
        for (final var element : collection) {
            result.add(this.deserializeValue(element, elementType));
        }
        return result;
    }

    private Map<Object, Object> deserializeMap(final Map<?, ?> map, final Type keyType, final Type valueType) {
        final var result = new LinkedHashMap<>();
        for (final var entry : map.entrySet()) {
            result.put(
                    this.deserializeValue(entry.getKey(), keyType),
                    this.deserializeValue(entry.getValue(), valueType)
            );
        }
        return result;
    }
}
