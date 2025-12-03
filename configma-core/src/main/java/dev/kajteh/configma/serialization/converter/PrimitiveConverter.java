package dev.kajteh.configma.serialization.converter;

public final class PrimitiveConverter {

    private PrimitiveConverter() {}

    public static Object convertString(
            final String s,
            final Class<?> rawType
    ) {
        if (rawType == boolean.class || rawType == Boolean.class) return Boolean.valueOf(s);

        if (rawType == int.class || rawType == Integer.class) return Integer.valueOf(s);

        if (rawType == long.class || rawType == Long.class) return Long.valueOf(s);

        if (rawType == float.class || rawType == Float.class) return Float.valueOf(s);

        if (rawType == double.class || rawType == Double.class) return Double.valueOf(s);

        return s;
    }

    public static Object convertNumber(final Number number, final Class<?> rawType) {
        if (rawType == int.class || rawType == Integer.class) return number.intValue();

        if (rawType == long.class || rawType == Long.class) return number.longValue();

        if (rawType == float.class || rawType == Float.class) return number.floatValue();

        if (rawType == double.class || rawType == Double.class) return number.doubleValue();

        if (rawType == short.class || rawType == Short.class) return number.shortValue();

        return number;
    }
}
