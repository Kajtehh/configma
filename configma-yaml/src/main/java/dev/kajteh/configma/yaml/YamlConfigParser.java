package dev.kajteh.configma.yaml;

import dev.kajteh.configma.ConfigParser;
import dev.kajteh.configma.ConfigNamingStyle;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

public class YamlConfigParser implements ConfigParser {

    private final Yaml yaml;

    public YamlConfigParser(final Yaml yaml) {
        this.yaml = yaml;
    }

    public YamlConfigParser() {
        final DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        this.yaml = new Yaml(dumperOptions);
    }

    @Override
    public Map<String, Object> load(final Reader reader) {
        return this.yaml.load(reader);
    }

    @Override
    public void write(final Writer writer, final Map<String, Object> values) {
        this.yaml.dump(values, writer);
    }

    @Override
    public ConfigNamingStyle getNamingStyle() {
        return ConfigNamingStyle.KEBAB;
    }
}
