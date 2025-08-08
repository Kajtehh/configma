package pl.kajteh.configma;

import org.bukkit.configuration.ConfigurationSection;
import pl.kajteh.configma.serialization.serializer.ObjectSerializer;
import pl.kajteh.configma.serialization.serializer.Serializer;
import pl.kajteh.configma.serialization.serializer.ValueSerializer;
import pl.kajteh.configma.serialization.data.SerializationData;
import pl.kajteh.configma.serialization.data.SerializedData;
import pl.kajteh.configma.type.ConfigTypeCache;
import pl.kajteh.configma.util.ConfigParseUtil;

import java.util.*;

public final class ConfigProcessor {

    private final Map<Class<?>, Serializer<?>> serializers = new HashMap<>();

    private final ConfigTypeCache typeCache;

    // todo add automatic serializer based on reflection

    ConfigProcessor(ConfigTypeCache typeCache, List<Serializer<?>> serializers) {
        this.typeCache = typeCache;

        serializers.forEach(serializer ->
                this.serializers.put(serializer.getType(), serializer));
    }

    public Object process(final Class<?> type, Object value) {
        if(type.isEnum()) return value.toString();

        final Serializer<?> serializer = this.findSerializer(type);

        if(serializer instanceof ObjectSerializer<?> objectSerializer) {
            value = this.serialize(objectSerializer, value);
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

    public Object processExisting(final String path, final Object value) {
        final Class<?> type = this.typeCache.getType(path);

        if(type.isEnum()) {
            return ConfigParseUtil.parseEnum(type, value);
        }

        final Serializer<?> serializer = this.findSerializer(type);

        if(value instanceof ConfigurationSection section && serializer instanceof ObjectSerializer<?> objectSerializer) {
            return this.deserialize(path, objectSerializer, section.getValues(false));
        }

        if(value instanceof Map<?, ?> map) {
            final Map<Object, Object> finalMap = new LinkedHashMap<>();

            for(final Map.Entry<?, ?> entry : map.entrySet()) {
                finalMap.put(
                        this.processExisting(path + ".key", entry.getKey()),
                        this.processExisting(path + ".value", entry.getValue())
                );
            }

            return finalMap;
        }

        if(value instanceof Collection<?> collection) {
            final Collection<Object> finalCollection = this.createEmptyCollection(collection);

            int i = 0;
            for (Object item : collection) {
                finalCollection.add(this.processExisting(path + "[" + i++ + "]", item));
            }

            return finalCollection;
        }

        if(serializer instanceof ValueSerializer<?> valueSerializer) return valueSerializer.deserialize(value);

        return value;
    }

    private Serializer<?> findSerializer(final Class<?> type) {
        return this.serializers.get(type);
    }

    @SuppressWarnings("unchecked")
    private <T> Object serialize(final ObjectSerializer<T> serializer, final Object value) {
        final SerializationData serializationData = new SerializationData(this);

        serializer.serialize(serializationData, (T) value);

        return serializationData.asMap();
    }

    @SuppressWarnings("unchecked")
    private <T> Object serializeValue(final ValueSerializer<T> serializer, final Object value) {
        return serializer.serialize((T) value);
    }

    private Object deserialize(final String path, final ObjectSerializer<?> serializer, final Map<String, Object> values) {
        return serializer.deserialize(new SerializedData(this, values, path));
    }

    private Collection<Object> createEmptyCollection(final Collection<?> collection) {
        return collection instanceof List
                ? new ArrayList<>()
                : new LinkedHashSet<>();
    }
}
