package dev.kajteh.configma.serialization.serializer.builtin;

import dev.kajteh.configma.serialization.context.DeserializationContext;
import dev.kajteh.configma.serialization.context.SerializationContext;
import dev.kajteh.configma.serialization.serializer.ObjectSerializer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecordSerializer implements ObjectSerializer<Record> {

    private final Map<Class<?>, Constructor<?>> constructorCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, RecordComponent[]> componentsCache = new ConcurrentHashMap<>();

    @Override
    public void serialize(@NotNull SerializationContext context, @NotNull Record record) {
        RecordComponent[] components = componentsCache.computeIfAbsent(record.getClass(), Class::getRecordComponents);

        for (RecordComponent component : components) {
            try {
                context.set(component.getName(), component.getAccessor().invoke(record));
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize record component: " + component.getName(), e);
            }
        }
    }

    @Override
    public Record deserialize(@NotNull DeserializationContext context) {
        final Class<?> clazz = context.getRawType();

        final RecordComponent[] components = this.componentsCache.computeIfAbsent(clazz, Class::getRecordComponents);
        final Constructor<?> constructor = this.constructorCache.computeIfAbsent(clazz, c -> {
            Class<?>[] paramTypes = new Class<?>[components.length];
            for (int i = 0; i < components.length; i++) {
                paramTypes[i] = components[i].getType();
            }
            try {
                return clazz.getDeclaredConstructor(paramTypes);
            } catch (final NoSuchMethodException e) {
                throw new RuntimeException("Canonical constructor not found for record: " + clazz.getName(), e);
            }
        });

        final Object[] args = new Object[components.length];
        for (int i = 0; i < components.length; i++) {
            args[i] = context.get(components[i].getName(), components[i].getType());
        }

        try {
            return (Record) constructor.newInstance(args);
        } catch (final Exception e) {
            throw new RuntimeException("Failed to instantiate record: " + clazz.getName(), e);
        }
    }

    @Override
    public boolean matches(@NotNull Class<?> type) {
        return type.isRecord();
    }
}