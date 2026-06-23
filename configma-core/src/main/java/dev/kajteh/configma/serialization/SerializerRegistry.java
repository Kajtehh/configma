package dev.kajteh.configma.serialization;

import dev.kajteh.configma.serialization.serializer.Serializer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SerializerRegistry {

    private final List<Serializer<?, ?>> serializers;
    private final Map<Class<?>, Serializer<?, ?>> serializerCache = new ConcurrentHashMap<>();

    public SerializerRegistry(final List<Serializer<?, ?>> serializers) {
        this.serializers = serializers;
    }

    @SuppressWarnings("unchecked")
    public <T> Serializer<T, ?> findSerializer(final Class<?> rawType) {
        return (Serializer<T, ?>) this.serializerCache.computeIfAbsent(rawType, this::getSerializer);
    }

    private @Nullable Serializer<?, ?> getSerializer(final Class<?> type) {
        return this.serializers.stream()
                .filter(s -> s.matches(type))
                .findFirst()
                .orElse(null);
    }
}
