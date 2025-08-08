package pl.kajteh.configma;

import pl.kajteh.configma.annotation.IgnoreField;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigFieldCache {

    private final ConcurrentHashMap<Class<?>, List<Field>> cache = new ConcurrentHashMap<>();

    public List<Field> getFields(Class<?> clazz) {
        return cache.computeIfAbsent(clazz, cls -> Arrays.stream(cls.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(IgnoreField.class))
                .filter(field -> !Modifier.isFinal(field.getModifiers()))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .toList());
    }
}
