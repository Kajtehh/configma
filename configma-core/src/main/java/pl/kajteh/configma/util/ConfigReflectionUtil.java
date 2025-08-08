package pl.kajteh.configma.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class ConfigReflectionUtil {

    private ConfigReflectionUtil() {
    }

    public static Class<?> getClassFromType(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class<?>) {
                return (Class<?>) rawType;
            }
        }
        throw new IllegalArgumentException("Cannot convert Type to Class: " + type);
    }
}