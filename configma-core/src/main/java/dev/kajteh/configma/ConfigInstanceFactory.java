package dev.kajteh.configma;

import java.lang.reflect.Constructor;

public final class ConfigInstanceFactory {

    private ConfigInstanceFactory() {}

    public static <T> T createInstance(final Class<T> type) {
        try {
            final Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);

            return constructor.newInstance();
        } catch (final NoSuchMethodException e) {
            throw new ConfigException("No default constructor found for config class: " + type.getName(), e);
        } catch (final Exception e) {
            throw new ConfigException("Failed to create config instance for: " + type.getName(), e);
        }
    }
}
