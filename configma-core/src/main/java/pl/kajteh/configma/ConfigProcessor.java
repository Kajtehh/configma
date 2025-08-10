package pl.kajteh.configma;

import org.bukkit.configuration.ConfigurationSection;
import pl.kajteh.configma.serialization.serializer.ObjectSerializer;
import pl.kajteh.configma.serialization.serializer.Serializer;
import pl.kajteh.configma.serialization.serializer.ValueSerializer;
import pl.kajteh.configma.serialization.data.SerializationData;
import pl.kajteh.configma.serialization.data.SerializedData;
import pl.kajteh.configma.util.ConfigParseUtil;

import java.util.*;

public final class ConfigProcessor {

    private final Map<Class<?>, Serializer<?>> serializers = new HashMap<>();

    ConfigProcessor(final List<Serializer<?>> serializers) {
        serializers.forEach(serializer ->
                this.serializers.put(serializer.getType(), serializer));
    }

    public Object process(final Class<?> type, Object value) {
        if(type.isEnum()) return value.toString();

        final Serializer<?> serializer = this.findSerializer(type);

        if(serializer instanceof ObjectSerializer<?> objectSerializer) {
            value = this.serialize(objectSerializer, value).asMap();
        }

        if(serializer instanceof ValueSerializer<?> valueSerializer) {
            value = this.serializeValue(valueSerializer, value);
        }

        if(value instanceof Map<?,?> map) {
            final Map<Object, Object> finalMap = new LinkedHashMap<>();

            for(final Map.Entry<?, ?> entry : map.entrySet()) {
                final Object k = entry.getKey();
                final Object v = entry.getValue();

                finalMap.put(
                        this.process(k.getClass(), k),
                        this.process(v.getClass(), v)
                );
            }

            return finalMap;
        }

        if(value instanceof Collection<?> collection) {
            final Collection<Object> finalCollection = this.createEmptyCollection(collection);

            for (final Object element : collection) {
                finalCollection.add(this.process(element.getClass(), element));
            }

            return finalCollection;
        }

        return value;
    }

    public Object processExisting(final Class<?> type, final Object value) {
        return this.processExisting(type, value, null);
    }

    public Object processExisting(final Class<?> type, final Object value, final List<Class<?>> genericTypes) {
        if(type.isEnum()) {
            return ConfigParseUtil.parseEnum(type, value);
        }

        final Serializer<?> serializer = this.findSerializer(type);

        if(value instanceof ConfigurationSection section) {
            final Map<String, Object> values = section.getValues(false);

            if(serializer instanceof ObjectSerializer<?> objectSerializer) {
                return this.deserialize(objectSerializer, values);
            }

            return this.processExisting(type, values, genericTypes);
        }

        if(genericTypes != null && !genericTypes.isEmpty()) {
            if (value instanceof Map<?, ?> map) {
                final Map<Object, Object> finalMap = new LinkedHashMap<>();

                for (final Map.Entry<?, ?> entry : map.entrySet()) {
                    finalMap.put(
                            this.processExisting(genericTypes.get(0), entry.getKey()),
                            this.processExisting(genericTypes.get(1), entry.getValue())
                    );
                }

                return finalMap;
            }

            if (value instanceof Collection<?> collection) {
                final Collection<Object> finalCollection = this.createEmptyCollection(collection);

                for (final Object item : collection) {
                    finalCollection.add(this.processExisting(genericTypes.get(0), item));
                }

                return finalCollection;
            }
        }

        if(serializer instanceof ValueSerializer<?> valueSerializer) return valueSerializer.deserialize(value);

        return value;
    }

    private Serializer<?> findSerializer(final Class<?> type) {
        return this.serializers.get(type);
    }

    @SuppressWarnings("unchecked")
    private <T> SerializationData serialize(final ObjectSerializer<T> serializer, final Object value) {
        final SerializationData serializationData = new SerializationData(this);

        serializer.serialize(serializationData, (T) value);

        return serializationData;
    }

    @SuppressWarnings("unchecked")
    private <T> Object serializeValue(final ValueSerializer<T> serializer, final Object value) {
        return serializer.serialize((T) value);
    }

    private Object deserialize(final ObjectSerializer<?> serializer, final Map<String, Object> values) {
        return serializer.deserialize(new SerializedData(this, values));
    }

    private Collection<Object> createEmptyCollection(final Collection<?> collection) {
        return collection instanceof List
                ? new ArrayList<>()
                : new LinkedHashSet<>();
    }
}
