package dev.kajteh.configma.serialization;

import dev.kajteh.configma.serialization.serializer.ObjectSerializer;
import dev.kajteh.configma.serialization.serializer.Serializer;
import dev.kajteh.configma.serialization.serializer.ValueSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public final class SerializationService {

    private final List<Serializer<?>> serializers;

    public SerializationService(final List<Serializer<?>> serializers) {
        this.serializers = serializers;
    }

    public <T> Object serializeValue(final T value, final Type type) {
        if (value == null) return null;
        if (value instanceof Enum<?> e) return e.name();

        final Class<?> rawType = getRawType(type);
        final Serializer<T> serializer = findSerializer(rawType);

        if (serializer instanceof ValueSerializer<T> valueSerializer)
            return valueSerializer.serialize(value);

        if (serializer instanceof ObjectSerializer<T> objectSerializer) {
            final var context = new SerializationContext(this);
            objectSerializer.serialize(context, value);
            return context.values();
        }

        if (value instanceof Collection<?> collection) {
            return serializeCollection(collection, getTypeArgument(type, 0));
        }

        if (value instanceof Map<?, ?> map) {
            return serializeMap(map, getTypeArgument(type, 0), getTypeArgument(type, 1));
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    public <T> T deserializeValue(final Object raw, final Type type) {
        if (raw == null) return null;

        final Class<?> rawType = getRawType(type);

        if (rawType.isEnum() && raw instanceof String s)
            return (T) Enum.valueOf(rawType.asSubclass(Enum.class), s);

        final Serializer<T> serializer = findSerializer(rawType);

        if (serializer instanceof ValueSerializer<T> valueSerializer)
            return valueSerializer.deserialize(raw);

        if (serializer instanceof ObjectSerializer<T> objectSerializer && raw instanceof Map<?, ?> map) {
            final var context = new DeserializationContext(this, (Map<String, Object>) map);
            return objectSerializer.deserialize(context);
        }

        return switch (raw) {
            case Collection<?> collection when Collection.class.isAssignableFrom(rawType) ->
                    (T) deserializeCollection(collection, getTypeArgument(type, 0), rawType);

            case Map<?, ?> map when Map.class.isAssignableFrom(rawType) ->
                    (T) deserializeMap(map, getTypeArgument(type, 0), getTypeArgument(type, 1));

            case Number number -> (T) convertNumber(number, rawType);

            default -> (T) raw;
        };
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

    @SuppressWarnings("unchecked")
    private <T> Serializer<T> findSerializer(Class<?> rawType) {
        return (Serializer<T>) serializers.stream()
                .filter(s -> s.matches(rawType))
                .findFirst()
                .orElse(null);
    }

    private Collection<Object> serializeCollection(final Collection<?> collection, final Type elementType) {
        final Collection<Object> result = collection instanceof List ? new ArrayList<>() : new LinkedHashSet<>();
        for (var element : collection) {
            result.add(this.serializeValue(element, elementType));
        }

        return result;
    }

    private Map<Object, Object> serializeMap(final Map<?, ?> map, final Type keyType, final Type valueType) {
        final Map<Object, Object> result = new LinkedHashMap<>();
        for (var entry : map.entrySet()) {
            result.put(
                    this.serializeValue(entry.getKey(), keyType),
                    this.serializeValue(entry.getValue(), valueType)
            );
        }

        return result;
    }

    private Collection<Object> deserializeCollection(final Collection<?> collection, final Type elementType, final Class<?> rawType) {
        final Collection<Object> result = List.class.isAssignableFrom(rawType) ? new ArrayList<>() : new LinkedHashSet<>();
        for (var element : collection) {
            result.add(this.deserializeValue(element, elementType));
        }

        return result;
    }

    private Map<Object, Object> deserializeMap(final Map<?, ?> map, final Type keyType, final Type valueType) {
        final Map<Object, Object> result = new LinkedHashMap<>();
        for (var entry : map.entrySet()) {
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
