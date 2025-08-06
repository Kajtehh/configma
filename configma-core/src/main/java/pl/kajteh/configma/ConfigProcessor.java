package pl.kajteh.configma;

import org.bukkit.configuration.ConfigurationSection;
import pl.kajteh.configma.serialization.ConfigSerializer;
import pl.kajteh.configma.util.ConfigParseUtil;
import pl.kajteh.configma.util.ConfigReflectionUtil;

import java.lang.reflect.Type;
import java.util.*;

public final class ConfigProcessor {

    private final Map<Class<?>, ConfigSerializer<?>> serializers = new HashMap<>();

    ConfigProcessor(List<ConfigSerializer<?>> serializers) {
        serializers.forEach(serializer ->
                this.serializers.put(serializer.getTargetType(), serializer));
    }

    public Object process(Class<?> type, Object value) {
        return serializeValue(type, value);
    }

    public Object processExisting(Class<?> type, Object value) {
        return deserializeValue(type, value);
    }

    private Object serializeValue(Class<?> type, Object value) {
        final ConfigSerializer<?> serializer = getSerializer(type);

        if(type.isEnum()) return ((Enum<?>) value).name();

        if (serializer != null) value = serialize(serializer, value);

        if (value instanceof Map<?, ?>) {
            final Map<?, ?> map = (Map<?, ?>) value;
            final Map<Object, Object> result = new LinkedHashMap<>();

            map.forEach((k, v) -> result.put(
                    serializeValue(k.getClass(), k),
                    serializeValue(v.getClass(), v)
            ));

            return result;
        }

        if (value instanceof Collection<?>) {
            final Collection<?> collection = (Collection<?>) value;
            final Collection<Object> result = value instanceof List ? new ArrayList<>() : new LinkedHashSet<>();

            collection.forEach(item -> result.add(serializeValue(item.getClass(), item)));

            return result;
        }

        return value;
    }

    private Object deserializeValue(Class<?> type, Object value) {
        final ConfigSerializer<?> serializer = getSerializer(type);

        if (value instanceof ConfigurationSection) {
            final ConfigurationSection section = (ConfigurationSection) value;
            final Map<String, Object> rawValues = section.getValues(false);
            final Map<Object, Object> result = new LinkedHashMap<>();

            rawValues.forEach((fieldName, rawValue) -> {
                final Class<?> fieldType = ConfigReflectionUtil.resolveFieldType(type, fieldName);
                final Type genericType = ConfigReflectionUtil.resolveGenericType(type, fieldName);

                Object processedValue = deserializeValue(fieldType, rawValue);

                final ConfigSerializer<?> fieldSerializer = getSerializer(fieldType);

                if (processedValue instanceof Map) {
                    final Map<?, ?> mapVal = (Map<?, ?>) processedValue;

                    if (fieldSerializer != null) {
                        processedValue = deserialize(fieldSerializer, fieldType, mapVal);
                    }
                }

                if (processedValue instanceof Collection) {
                    final Collection<?> colVal = (Collection<?>) processedValue;

                    if (genericType != null) {
                        final Class<?> elementType = ConfigReflectionUtil.extractGenericClass(genericType);
                        final Collection<Object> newCollection = createEmptyCollection(colVal);

                        for (Object item : colVal) {
                            newCollection.add(deserializeValue(elementType, item));
                        }

                        processedValue = newCollection;
                    }
                }

                if(fieldType.isEnum()) {
                    processedValue = ConfigParseUtil.parseEnum(type, processedValue);
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

            collection.forEach(item -> result.add(deserializeValue(item.getClass(), item)));

            return result;
        }

        if(type.isEnum()) return ConfigParseUtil.parseEnum(type, value);

        return serializer != null
                ? this.deserialize(serializer, type, value)
                : value;
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
