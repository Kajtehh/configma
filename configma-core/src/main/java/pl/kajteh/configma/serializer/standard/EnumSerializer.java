package pl.kajteh.configma.serializer.standard;

import pl.kajteh.configma.serializer.ConfigSerializer;

public class EnumSerializer<T extends Enum<T>> implements ConfigSerializer<T> {

    @Override
    public Class<?> getTargetType() {
        return Enum.class;
    }

    @Override
    public Object serialize(T anEnum) {
        return anEnum.name();
    }

    @Override
    public T deserialize(Class<T> type, Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Expected a String to deserialize enum");
        }

        return Enum.valueOf(type, (String) value);
    }

    @Override
    public Boolean matchesType(Class<?> clazz) {
        return clazz.isEnum();
    }
}
