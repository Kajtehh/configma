package pl.kajteh.configma;

import pl.kajteh.configma.serialization.SerializationService;
import pl.kajteh.configma.serialization.serializer.ObjectSerializer;
import pl.kajteh.configma.serialization.serializer.Serializer;
import pl.kajteh.configma.serialization.serializer.ValueSerializer;

import java.util.*;

public final class ConfigProcessor {

    private final SerializationService serializationService;

    public ConfigProcessor(final List<Serializer> serializers) {
        this.serializationService = new SerializationService(this, serializers);
    }

    public Object process(final Class<?> type, Object value) {
        if(type.isEnum()) return value.toString();

        final Serializer serializer = this.serializationService.findSerializer(type);

        if(serializer != null) value = this.serializationService.serialize(serializer, value);

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
            return this.parseEnum(type, value);
        }

        final Serializer serializer = this.serializationService.findSerializer(type);

        if(genericTypes != null && !genericTypes.isEmpty()) {
            if (value instanceof Map<?, ?> map) {
                final Map<String, Object> finalMap = new LinkedHashMap<>();

                for (final Map.Entry<?, ?> entry : map.entrySet()) {
                    finalMap.put(
                            (String) this.processExisting(genericTypes.get(0), entry.getKey()), // test cast
                            this.processExisting(genericTypes.get(1), entry.getValue())
                    );
                }

                if(serializer instanceof ObjectSerializer<?> objectSerializer) {
                    return this.serializationService.deserializeObject(objectSerializer, finalMap);
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

        if(serializer instanceof ValueSerializer<?> valueSerializer) {
            return valueSerializer.deserialize(value);
        }

        return value;
    }

    private Collection<Object> createEmptyCollection(final Collection<?> collection) {
        return collection instanceof List
                ? new ArrayList<>()
                : new LinkedHashSet<>();
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> T parseEnum(final Class<?> enumType, final Object value) {
        if (!(value instanceof String string)) {
            throw new IllegalArgumentException("Expected String for enum but got: " + value.getClass().getName());
        }

        return Enum.valueOf((Class<T>) enumType, string);
    }
}