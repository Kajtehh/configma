package pl.kajteh.configma.serialization;

import pl.kajteh.configma.ConfigProcessor;
import pl.kajteh.configma.serialization.serializer.ObjectSerializer;
import pl.kajteh.configma.serialization.serializer.Serializer;
import pl.kajteh.configma.serialization.serializer.ValueSerializer;

import java.util.List;
import java.util.Map;

public final class SerializationService {

    private final ConfigProcessor configProcessor;
    private final List<Serializer> serializers;

    public SerializationService(final ConfigProcessor configProcessor, final List<Serializer> serializers) {
        this.serializers = serializers;
        this.configProcessor = configProcessor;
    }

    public Serializer findSerializer(final Class<?> type) {
        return this.serializers.stream().filter(serializer -> serializer.matches(type)).findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <T> Object serialize(final Serializer serializer, final Object value) {
        if(serializer instanceof ObjectSerializer<T> objectSerializer) {
            final SerializationData serializationData = new SerializationData(this.configProcessor);

            objectSerializer.serialize(serializationData, (T) value);

            return serializationData.asMap();
        }

        if(serializer instanceof ValueSerializer<T> valueSerializer) return valueSerializer.serialize((T) value);

        return value;
    }

    public Object deserializeObject(final ObjectSerializer<?> serializer, final Map<String, Object> values) {
        return serializer.deserialize(new SerializedData(this.configProcessor, values));
    }
}
