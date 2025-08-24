package pl.kajteh.configma.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import pl.kajteh.configma.ConfigProvider;
import pl.kajteh.configma.serialization.serializer.Serializer;

import java.io.File;
import java.util.List;

public final class YamlConfigProvider<T> extends ConfigProvider<T> {

    private final YamlConfigMapper configMapper;

    YamlConfigProvider(final T instance, final File configFile, final List<Serializer> serializers) {
        super(instance, configFile, serializers);

        final DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        final Yaml yaml = new Yaml(options);
        this.configMapper = new YamlConfigMapper(serializers, yaml);
    }

    private void syncFields(final boolean writeMode) {
        this.configMapper.syncFields(this.instance.getClass(), this.instance, writeMode);
    }

    @Override
    protected void save(final boolean writeMode) { // tu mamy blad bo gdy false to nie writuje i huj jest xdd
        this.syncFields(writeMode);
        if(writeMode) this.configMapper.write(this.configFile);
    }

    @Override
    protected void reload() {
        this.configMapper.load(this.configFile);
        this.syncFields(false);
    }
}
