package dev.kajteh.configma.yaml;

import dev.kajteh.configma.ConfigContext;
import dev.kajteh.configma.ConfigException;
import dev.kajteh.configma.ConfigParser;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

public class YamlConfigParser implements ConfigParser {

    private final Yaml yaml;

    private static final String COMMENT_PREFIX = "# ";

    private YamlConfigParser(final Yaml yaml) {
        this.yaml = yaml;
    }

    private YamlConfigParser() {
        final DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        this.yaml = new Yaml(dumperOptions);
    }

    public static YamlConfigParser standard() {
        return new YamlConfigParser();
    }

    public static YamlConfigParser of(final Yaml yaml) {
        return new YamlConfigParser(yaml);
    }

    @Override
    public Map<String, Object> load(final Reader reader) {
        return this.yaml.load(reader);
    }

    @Override
    public void write(
            final Writer writer,
            final ConfigContext context,
            final Map<String, Object> values
    ) {
        try {

            if (context.header() != null) {
                for (final var line : context.header()) {
                    writer.write(COMMENT_PREFIX + line + System.lineSeparator());
                }
                this.writeSpacing(context, writer);
            }

            String lastRootField = null;

            for (final var yamlLine : this.yaml.dump(values).split("\n")) {

                final var fieldName = this.extractFieldName(yamlLine);
                final var isRootField = !yamlLine.startsWith(" ") && !yamlLine.startsWith("-");

                if (isRootField && lastRootField != null && !lastRootField.equals(fieldName)) {
                    this.writeSpacing(context, writer);
                }

                if (isRootField) {
                    final var comments = context.comments().get(fieldName);
                    if (comments != null) {
                        for (final var comment : comments) {
                            writer.write(COMMENT_PREFIX + comment + System.lineSeparator());
                        }
                    }
                }

                final var inlineComment = context.inlineComments().get(fieldName);
                writer.write(yamlLine + (inlineComment != null && isRootField ? " " + COMMENT_PREFIX + inlineComment : ""));
                writer.write(System.lineSeparator());

                if (isRootField) lastRootField = fieldName;
            }

            if (context.footer() != null) {
                this.writeSpacing(context, writer);

                for (final var line : context.footer()) {
                    writer.write(COMMENT_PREFIX + line);
                }
            }

        } catch (final IOException e) {
            throw new ConfigException("Failed to write configuration with decorations", e);
        }
    }

    private void writeSpacing(final ConfigContext context, final Writer writer) throws IOException {
        for (int i = 0; i < context.spacing(); i++) {
            writer.write(System.lineSeparator());
        }
    }

    private String extractFieldName(String yamlLine) {
        if (yamlLine == null) return null;

        yamlLine = yamlLine.trim();
        if (yamlLine.isEmpty() || yamlLine.startsWith(COMMENT_PREFIX.trim())) return null;

        final var colonIndex = yamlLine.indexOf(':');
        if (colonIndex == -1) return null;

        return yamlLine.substring(0, colonIndex).trim();
    }

    @Override
    public String formatField(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }

        return name.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }

    @Override
    public boolean commentsSupported() {
        return true;
    }
}
