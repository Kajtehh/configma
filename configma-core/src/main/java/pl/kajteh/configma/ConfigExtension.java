package pl.kajteh.configma;

import org.bukkit.configuration.file.YamlConfiguration;

import java.lang.reflect.Field;

public interface ConfigExtension {

    default void onLoad(Class<?> configClass, YamlConfiguration configuration) {

    }

    default void onFieldSaved(Class<?> configClass, YamlConfiguration configuration, String path, Field field, Object value) {

    }
}
