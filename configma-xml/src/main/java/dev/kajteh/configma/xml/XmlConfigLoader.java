package dev.kajteh.configma.xml;

import dev.kajteh.configma.ConfigContext;
import dev.kajteh.configma.ConfigLoader;
import dev.kajteh.configma.exception.ConfigException;
import org.jetbrains.annotations.NotNull;
import tools.jackson.core.type.TypeReference;
import tools.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

/**
 * Experimental XML loader - do not use in production environment!
 */
@Deprecated
public class XmlConfigLoader implements ConfigLoader {

    // TODO: add custom lists and maps handler

    private final XmlMapper mapper;

    private static final String ROOT_NAME = "config";

    public XmlConfigLoader() {
        this.mapper = new XmlMapper();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> load(Reader reader, ConfigContext context) {
        try {
            if (reader.read() == -1) return Map.of(ROOT_NAME, Map.of());

            final Map<String, Object> root = this.mapper.readValue(reader, new TypeReference<>() {});

            final var inner = root.get(ROOT_NAME);

            // todo null check

            return (inner instanceof Map<?, ?> innerMap) ? (Map<String, Object>) innerMap : root;
        } catch (final IOException e) {
            throw new ConfigException("Failed to parse XML configuration", e);
        }
    }


    @Override
    public void write(Writer writer, Map<String, Object> values, ConfigContext context) {
        final var xml = this.mapper
                .writerWithDefaultPrettyPrinter()
                .withRootName(ROOT_NAME)
                .writeValueAsString(values);


        try {
            writer.write(xml);
        } catch (final IOException e) {
            throw new ConfigException("Failed to write xml configuration: ", e);
        }
    }

    @Override
    public @NotNull String fileExtension() {
        return "xml";
    }
}
