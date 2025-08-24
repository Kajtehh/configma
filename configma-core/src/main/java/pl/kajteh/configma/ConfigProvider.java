package pl.kajteh.configma;

import pl.kajteh.configma.serialization.serializer.Serializer;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class ConfigProvider<T> {

    protected final T instance;
    protected final File configFile;
    protected final List<Serializer> serializers;

    protected ConfigProvider(final T instance, final File configFile, final List<Serializer> serializers) {
        this.instance = instance;
        this.configFile = configFile;
        this.serializers = serializers;

        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs(); // todo
                configFile.createNewFile(); // todo
            } catch (final IOException e) {
                throw new ConfigException("eee", e); // todo
            }
        }
    }

    protected abstract void save(final boolean writeMode);
    protected abstract void reload();
}
