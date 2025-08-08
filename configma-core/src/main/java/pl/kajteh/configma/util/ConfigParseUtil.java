package pl.kajteh.configma.util;

public class ConfigParseUtil {

    private ConfigParseUtil() {
    }

    @SuppressWarnings("unchecked")
    public static  <T extends Enum<T>> T parseEnum(Class<?> enumType, Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Expected String for enum but got: " + value.getClass().getName());
        }

        return Enum.valueOf((Class<T>) enumType, (String) value);
    }

}
