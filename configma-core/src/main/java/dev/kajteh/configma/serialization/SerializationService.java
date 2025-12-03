package dev.kajteh.configma.serialization;

import dev.kajteh.configma.exception.ConfigException;
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

        final var serializer = this.serializerRegistry.findSerializer(getRawType(type));

        if(serializer != null)
            return switch (serializer) {
                case TypeSerializer<?, ?> typeSerializer -> asTypeSerializer(typeSerializer).serialize(value);
                case ObjectSerializer<?> objectSerializer -> {
                    final var context = new SerializationContext(this);
                    asObjectSerializer(objectSerializer).serialize(context, value);
                    yield context.values();
                }
                default -> throw new ConfigException("Unsupported serializer type: " + serializer.getClass());
            };

        return switch (value) {
            case Enum<?> e -> e.name();
            case Collection<?> collection -> this.serializeCollection(collection, getTypeArgument(type, 0));
            case Map<?, ?> map -> this.serializeMap(map, getTypeArgument(type, 0), getTypeArgument(type, 1));
            default -> value;
        };
    }

    @SuppressWarnings("unchecked")
    public <T> T deserializeValue(final Object raw, final Type type) {
        if (raw == null) return null;

        final var rawType = getRawType(type);

        if (rawType.isEnum() && raw instanceof String s) {
            return (T) Enum.valueOf(rawType.asSubclass(Enum.class), s);
        }

        final var serializer = this.serializerRegistry.findSerializer(rawType);

        if(serializer != null)
            return (T) switch (serializer) {
                case TypeSerializer<?, ?> typeSerializer -> asTypeSerializer(typeSerializer).deserialize(raw);
                case ObjectSerializer<?> objectSerializer when raw instanceof Map<?, ?> map -> asObjectSerializer(objectSerializer).deserialize(
                        new DeserializationContext(this, (Map<String, Object>) map)
                );
                default -> throw new ConfigException("Unsupported serializer type: " + serializer.getClass());
            };

        return (T) switch (raw) {
            case Collection<?> collection -> this.deserializeCollection(collection, getTypeArgument(type, 0), rawType);
            case Map<?, ?> map -> this.deserializeMap(map, getTypeArgument(type, 0), getTypeArgument(type, 1));
            case Number number -> this.convertNumber(number, rawType);
            case String s -> this.handleString(s, rawType);
            default -> raw;
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> ObjectSerializer<T> asObjectSerializer(final Serializer<?, ?> serializer) {
        return (ObjectSerializer<T>) serializer;
    }

    @SuppressWarnings("unchecked")
    private static <T, R> TypeSerializer<T, R> asTypeSerializer(final Serializer<?, ?> serializer) {
        return (TypeSerializer<T, R>) serializer;
    }

    private static Class<?> getRawType(final Type type) {
        if (type instanceof Class<?> c) return c;
        if (type instanceof ParameterizedType pt) return (Class<?>) pt.getRawType();
        return Object.class;
    }

    private static Type getTypeArgument(final Type type, final int index) {
        if (type instanceof ParameterizedType pt && pt.getActualTypeArguments().length > index)
            return pt.getActualTypeArguments()[index];
        return Object.class;
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

    private Object handleString(final String s, final Class<?> rawType) {
        if (rawType == boolean.class || rawType == Boolean.class) return Boolean.valueOf(s);
        if (rawType == int.class || rawType == Integer.class) return Integer.valueOf(s);
        if (rawType == long.class || rawType == Long.class) return Long.valueOf(s);
        if (rawType == float.class || rawType == Float.class) return Float.valueOf(s);
        if (rawType == double.class || rawType == Double.class) return Double.valueOf(s);
        return s;
    }

    private Object convertNumber(final Number number, final Class<?> rawType) {
        if (rawType == int.class || rawType == Integer.class) return number.intValue();
        if (rawType == long.class || rawType == Long.class) return number.longValue();
        if (rawType == float.class || rawType == Float.class) return number.floatValue();
        if (rawType == double.class || rawType == Double.class) return number.doubleValue();
        if (rawType == short.class || rawType == Short.class) return number.shortValue();
        return number;
    }
}
