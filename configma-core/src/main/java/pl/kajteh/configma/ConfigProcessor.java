package pl.kajteh.configma;

import org.bukkit.configuration.ConfigurationSection;
import pl.kajteh.configma.serializer.ConfigSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public final class ConfigProcessor {

    private final Map<Class<?>, ConfigSerializer<?>> serializers = new HashMap<>();

    public ConfigProcessor(List<ConfigSerializer<?>> serializers) {
        serializers.forEach(serializer ->
                this.serializers.put(serializer.getTargetType(), serializer));
    }

    public Object process(Class<?> type, Object value) {
        return this.serializeValue(type, value);
    }

    public Object processExisting(Class<?> type, Object value) {
        return this.deserializeValue(type, value);
    }

    private Object serializeValue(Class<?> type, Object value) {
        final ConfigSerializer<?> serializer = this.getSerializer(type);

        if (serializer != null) value = this.serialize(serializer, value);

        if (value instanceof Map<?, ?>) {
            final Map<?, ?> map = (Map<?, ?>) value;
            final Map<Object, Object> result = new LinkedHashMap<>();

            map.forEach((k, v) -> result.put(
                    this.serializeValue(k.getClass(), k),
                    this.serializeValue(v.getClass(), v)
            ));

            return result;
        }

        if (value instanceof Collection<?>) {
            final Collection<?> collection = (Collection<?>) value;
            final Collection<Object> result = value instanceof List ? new ArrayList<>() : new LinkedHashSet<>();

            collection.forEach(item -> result.add(this.serializeValue(item.getClass(), item)));

            return result;
        }

        return value;
    }

    private Object deserializeValue(Class<?> type, Object value) {
        final ConfigSerializer<?> serializer = this.getSerializer(type);

        if (value instanceof ConfigurationSection) {
            final ConfigurationSection section = (ConfigurationSection) value;
            final Map<String, Object> rawValues = section.getValues(false);
            final Map<Object, Object> result = new LinkedHashMap<>();

            rawValues.forEach((fieldName, rawValue) -> {
                final Class<?> fieldType = this.resolveFieldType(type, fieldName);
                final Type genericType = this.resolveGenericType(type, fieldName);

                Object processedValue = this.deserializeValue(fieldType, rawValue);

                final ConfigSerializer<?> fieldSerializer = this.getSerializer(fieldType);

                if (processedValue instanceof Map) {
                    final Map<?, ?> mapVal = (Map<?, ?>) processedValue;

                    if (fieldSerializer != null) {
                        processedValue = this.deserialize(fieldSerializer, fieldType, mapVal);
                    }
                }

                if (processedValue instanceof Collection) {
                    final Collection<?> colVal = (Collection<?>) processedValue;

                    if (genericType != null) {
                        final Class<?> elementType = this.extractGenericClass(genericType);
                        final Collection<Object> newCollection = this.createEmptyCollection(colVal);

                        for (Object item : colVal) {
                            newCollection.add(this.deserializeValue(elementType, item));
                        }

                        processedValue = newCollection;
                    }
                }

                result.put(fieldName, processedValue);
            });

            return serializer != null
                    ? this.deserialize(serializer, type, result)
                    : result;
        }

        if (value instanceof Collection<?>) {
            final Collection<?> collection = (Collection<?>) value;
            final Collection<Object> result = createEmptyCollection(collection);

            collection.forEach(item -> result.add(this.deserializeValue(item.getClass(), item)));

            return result;
        }

        return serializer != null
                ? this.deserialize(serializer, type, value)
                : value;
    }

    private Class<?> resolveFieldType(Class<?> parentClass, String fieldName) {
        try {
            return parentClass.getDeclaredField(fieldName).getType();
        } catch (NoSuchFieldException e) {
            return Object.class;
        }
    }

    private Type resolveGenericType(Class<?> parentClass, String fieldName) {
        try {
            return parentClass.getDeclaredField(fieldName).getGenericType();
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private Class<?> extractGenericClass(Type type) {
        if (type instanceof ParameterizedType) {
            final ParameterizedType paramType = (ParameterizedType) type;
            final Type arg = paramType.getActualTypeArguments()[0];

            if (arg instanceof Class<?>) return (Class<?>) arg;
        }
        return Object.class;
    }

    private Collection<Object> createEmptyCollection(Collection<?> base) {
        return base instanceof List ? new ArrayList<>() : new LinkedHashSet<>();
    }

    private ConfigSerializer<?> getSerializer(Class<?> type) {
        return this.serializers.get(type);
    }

    @SuppressWarnings("unchecked")
    private <T> Object serialize(ConfigSerializer<T> serializer, Object value) {
        return serializer.serialize((T) value);
    }

    @SuppressWarnings("unchecked")
    private <T> Object deserialize(ConfigSerializer<T> serializer, Class<?> type, Object value) {
        return serializer.deserialize((Class<T>) type, value);
    }
}
