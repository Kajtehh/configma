package dev.kajteh.configma.serialization.util;

import dev.kajteh.configma.exception.ConfigException;
import dev.kajteh.configma.serialization.serializer.ObjectSerializer;
import dev.kajteh.configma.serialization.serializer.Serializer;
import dev.kajteh.configma.serialization.serializer.TypeSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class TypeUtil {

    private TypeUtil() {}

    public static Class<?> rawType(final Type type) {
        if (type instanceof Class<?> c) return c;
        if (type instanceof ParameterizedType pt) return (Class<?>) pt.getRawType();

        return Object.class;
    }

    public static Type typeArgument(final Type type, final int index) {
        if (type instanceof ParameterizedType pt && pt.getActualTypeArguments().length > index)
            return pt.getActualTypeArguments()[index];

        return Object.class;
    }

    @SuppressWarnings("unchecked")
    public static <T> ObjectSerializer<T> asObjectSerializer(final Serializer<?, ?> serializer) {
        return (ObjectSerializer<T>) serializer;
    }

    @SuppressWarnings("unchecked")
    public static <T, R> TypeSerializer<T, R> asTypeSerializer(final Serializer<?, ?> serializer) {
        return (TypeSerializer<T, R>) serializer;
    }

    public static <T> T createInstance(final Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (final Exception e) {
            throw new ConfigException("Cannot create an instance for : " + type.getName(), e);
        }
    }
}
