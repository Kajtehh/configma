package pl.kajteh.configma.yaml;

import org.yaml.snakeyaml.Yaml;
import pl.kajteh.configma.ConfigException;
import pl.kajteh.configma.ConfigMapper;
import pl.kajteh.configma.serialization.serializer.Serializer;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;

public final class YamlConfigMapper extends ConfigMapper {

    private final Yaml yaml;

    public YamlConfigMapper(final List<Serializer> serializers, final Yaml yaml) {
        super(serializers);
        this.yaml = yaml;
    }

    @Override
    protected void load(final File file) {
        try (final InputStream inputStream = new FileInputStream(file)) {
            this.values = this.yaml.load(inputStream);

            if(this.values == null) this.values = new LinkedHashMap<>();
        } catch (final IOException e) {
            throw new ConfigException(e);
        }
    }

    @Override
    protected void write(final File file) {
        try (final Writer writer = new FileWriter(file)) {
            this.yaml.dump(this.values, writer);
        } catch (final IOException e) {
            throw new ConfigException(e);
        }
    }
}
