package pl.kajteh.configma;

import org.bukkit.configuration.ConfigurationSection;
import pl.kajteh.configma.serialize.ConfigSerializer;

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

        if (value instanceof Map<?, ?> map) {
            final Map<Object, Object> result = new LinkedHashMap<>();

            map.forEach((k, v) -> result.put(
                    this.serializeValue(k.getClass(), k),
                    this.serializeValue(v.getClass(), v)
            ));

            return result;
        }

        if (value instanceof Collection<?> collection) {
            final Collection<Object> result = value instanceof List ? new ArrayList<>() : new LinkedHashSet<>();

            collection.forEach(item -> result.add(this.serializeValue(item.getClass(), item)));

            return result;
        }

        return value;
    }

    private Object deserializeValue(Class<?> type, Object value) {
        final ConfigSerializer<?> serializer = this.getSerializer(type);

        if (value instanceof ConfigurationSection section) {
            final Map<String, Object> rawValues = section.getValues(false);
            final Map<Object, Object> result = new LinkedHashMap<>();

            rawValues.forEach((fieldName, rawVal) -> {
                final Object key = fieldName;
                final Class<?> fieldType = this.resolveFieldType(type, fieldName);
                final Type genericType = this.resolveGenericType(type, fieldName);

                Object processedValue = this.deserializeValue(fieldType, rawVal);

                final ConfigSerializer<?> fieldSerializer = this.getSerializer(fieldType);

                if (processedValue instanceof Map<?, ?> mapVal && fieldSerializer != null) {
                    processedValue = this.deserialize(fieldSerializer, fieldType, mapVal);
                }

                if (processedValue instanceof Collection<?> colVal && genericType != null) {
                    final Class<?> elementType = this.extractGenericClass(genericType);
                    final Collection<Object> newCollection = this.createEmptyCollection(colVal);

                    for (Object item : colVal) {
                        newCollection.add(this.deserializeValue(elementType, item));
                    }

                    processedValue = newCollection;
                }

                result.put(key, processedValue);
            });

            return serializer != null
                    ? this.deserialize(serializer, type, result)
                    : result;
        }

        if (value instanceof Collection<?> collection) {
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
        if (type instanceof ParameterizedType paramType) {
            final Type arg = paramType.getActualTypeArguments()[0];

            if (arg instanceof Class<?> clazz) {
                return clazz;
            }
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
