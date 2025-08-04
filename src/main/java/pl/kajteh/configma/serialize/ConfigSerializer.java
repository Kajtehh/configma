package pl.kajteh.configma.serialize;

public interface ConfigSerializer<T> {
    Class<?> getTargetType();
    Object serialize(T t);
    T deserialize(Class<T> type, Object value);
    default Boolean matchesType(Class<?> type) {
        return null;
    }
}
