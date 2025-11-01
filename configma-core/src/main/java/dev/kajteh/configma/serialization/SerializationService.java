package dev.kajteh.configma.serialization;

import dev.kajteh.configma.serialization.serializer.ObjectSerializer;
import dev.kajteh.configma.serialization.serializer.Serializer;
import dev.kajteh.configma.serialization.serializer.ValueSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class SerializationService {

    private final List<Serializer<?>> serializers;
    private final Map<Class<?>, Serializer<?>> serializerCache = new ConcurrentHashMap<>();

    public SerializationService(final List<Serializer<?>> serializers) {
        this.serializers = serializers;
    }

    public <T> Object serializeValue(final T value, final Type type) {
        if(value == null) return null;

        final Serializer<T> serializer = this.findSerializer(getRawType(type));

        if (serializer instanceof ValueSerializer<T> valueSerializer)
            return valueSerializer.serialize(value);

        if (serializer instanceof ObjectSerializer<T> objectSerializer) {
            final var context = new SerializationContext(this);
            objectSerializer.serialize(context, value);
            return context.values();
        }

        return switch (value) {
            case final Enum<?> e -> e.name();

            case final Collection<?> collection -> this.serializeCollection(collection, getTypeArgument(type, 0));

            case final Map<?, ?> map -> this.serializeMap(map, getTypeArgument(type, 0), getTypeArgument(type, 1));

            default -> value;
        };
    }

    @SuppressWarnings("unchecked")
    public <T> T deserializeValue(final Object raw, final Type type) {
        if (raw == null) return null;

        final Class<?> rawType = getRawType(type);

        if (rawType.isEnum() && raw instanceof String s)
            return (T) Enum.valueOf(rawType.asSubclass(Enum.class), s);

        final Serializer<T> serializer = this.findSerializer(rawType);

        if (serializer instanceof ValueSerializer<T> valueSerializer)
            return valueSerializer.deserialize(raw);

        if (serializer instanceof ObjectSerializer<T> objectSerializer && raw instanceof Map<?, ?> map) {
            final var context = new DeserializationContext(this, (Map<String, Object>) map);
            return objectSerializer.deserialize(context);
        }

        return switch (raw) {
            case final Collection<?> collection when Collection.class.isAssignableFrom(rawType) ->
                    (T) this.deserializeCollection(collection, getTypeArgument(type, 0), rawType);

            case final Map<?, ?> map when Map.class.isAssignableFrom(rawType) ->
                    (T) this.deserializeMap(map, getTypeArgument(type, 0), getTypeArgument(type, 1));

            case final Number number -> (T) convertNumber(number, rawType);

            default -> (T) raw;
        };
    }

    @SuppressWarnings("unchecked")
    private <T> Serializer<T> findSerializer(final Class<?> rawType) {
        return (Serializer<T>) this.serializerCache.computeIfAbsent(rawType, type ->
                this.serializers.stream().filter(s -> s.matches(type)).findFirst().orElse(null)
        );
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
        final Collection<Object> result = collection instanceof List ? new ArrayList<>() : new LinkedHashSet<>();
        for (final var element : collection) {
            result.add(this.serializeValue(element, elementType));
        }

        return result;
    }

    private Map<Object, Object> serializeMap(final Map<?, ?> map, final Type keyType, final Type valueType) {
        final Map<Object, Object> result = new LinkedHashMap<>();

        for (final var entry : map.entrySet()) {
            result.put(
                    this.serializeValue(entry.getKey(), keyType),
                    this.serializeValue(entry.getValue(), valueType)
            );
        }

        return result;
    }

    private Collection<Object> deserializeCollection(final Collection<?> collection, final Type elementType, final Class<?> rawType) {
        final Collection<Object> result = List.class.isAssignableFrom(rawType) ? new ArrayList<>() : new LinkedHashSet<>();
        for (final var element : collection) {
            result.add(this.deserializeValue(element, elementType));
        }

        return result;
    }

    private Map<Object, Object> deserializeMap(final Map<?, ?> map, final Type keyType, final Type valueType) {
        final Map<Object, Object> result = new LinkedHashMap<>();
        for (final var entry : map.entrySet()) {
            result.put(
                    this.deserializeValue(entry.getKey(), keyType),
                    this.deserializeValue(entry.getValue(), valueType)
            );
        }

        return result;
    }

    private Object convertNumber(final Number number, final Class<?> rawType) {
        if (rawType == int.class || rawType == Integer.class) return number.intValue();
        if (rawType == long.class || rawType == Long.class) return number.longValue();
        if (rawType == float.class || rawType == Float.class) return number.floatValue();
        if (rawType == double.class || rawType == Double.class) return number.doubleValue();
        return number;
    }
}
