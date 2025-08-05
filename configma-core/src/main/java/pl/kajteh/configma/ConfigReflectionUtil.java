package pl.kajteh.configma;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class ConfigReflectionUtil {

    private ConfigReflectionUtil() {
    }

    public static Class<?> resolveFieldType(Class<?> parentClass, String fieldName) {
        try {
            return parentClass.getDeclaredField(fieldName).getType();
        } catch (NoSuchFieldException e) {
            return Object.class;
        }
    }

    public static Type resolveGenericType(Class<?> parentClass, String fieldName) {
        try {
            return parentClass.getDeclaredField(fieldName).getGenericType();
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static Class<?> extractGenericClass(Type type) {
        if (type instanceof ParameterizedType) {
            final ParameterizedType paramType = (ParameterizedType) type;
            final Type arg = paramType.getActualTypeArguments()[0];

            if (arg instanceof Class<?>) return (Class<?>) arg;
        }
        return Object.class;
    }
}