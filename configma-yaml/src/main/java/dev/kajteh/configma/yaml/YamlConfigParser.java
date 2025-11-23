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

    private YamlConfigParser() {
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(options);
    }

    private YamlConfigParser(Yaml yaml) {
        this.yaml = yaml;
    }

    public static YamlConfigParser standard() {
        return new YamlConfigParser();
    }

    public static YamlConfigParser of(Yaml yaml) {
        return new YamlConfigParser(yaml);
    }

    @Override
    public String formatField(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }

    @Override
    public Map<String, Object> load(Reader reader) {
        return this.yaml.load(reader);
    }

    @Override
    public void write(Writer writer, Map<String, Object> values, ConfigContext context) {
        try {
            if (context.header() != null) {
                for (final var line : context.header())
                    writer.write(COMMENT_PREFIX + line + System.lineSeparator());

                this.writeSpacing(context, writer);
            }

            String lastRootField = null;

            for (final var yamlLine : this.yaml.dump(values).split("\n")) {
                if (yamlLine.trim().isEmpty()) continue;

                final var isRootField = !yamlLine.startsWith(" ") && !yamlLine.startsWith("-");

                final var fieldName = this.extractFieldName(yamlLine);
                if (isRootField && lastRootField != null && !lastRootField.equals(fieldName)) {
                    this.writeSpacing(context, writer);
                }

                final var fullField = isRootField
                        ? fieldName
                        : (lastRootField != null ? lastRootField + "." + fieldName : fieldName);

                this.applyComments(context, fullField, writer, yamlLine);
                this.applyInlineComment(context, fullField, writer, yamlLine);

                if (isRootField) lastRootField = fieldName;
            }

            if (context.footer() != null) {
                this.writeSpacing(context, writer);

                for (final var line : context.footer())
                    writer.write(COMMENT_PREFIX + line + System.lineSeparator());
            }

        } catch (final IOException e) {
            throw new ConfigException("Failed to write configuration with decorations", e);
        }
    }

    private void applyComments(ConfigContext context, String field, Writer writer, String yamlLine) throws IOException {
        final var comments = context.comments().get(field);
        if (comments == null) return;

        String indent = "";

        final int colon = yamlLine.indexOf(':');
        if (colon > 0) indent = yamlLine.substring(0, yamlLine.indexOf(yamlLine.trim())).replaceAll("[^ ]", "");

        for (final var comment : comments)
            writer.write(indent + COMMENT_PREFIX + comment + System.lineSeparator());
    }

    private void applyInlineComment(ConfigContext context, String field, Writer writer, String yamlLine) throws IOException {
        final var comment = context.inlineComments().get(field);
        if (comment != null) {
            writer.write(yamlLine + " " + COMMENT_PREFIX + comment + System.lineSeparator());
            return;
        }

        writer.write(yamlLine + System.lineSeparator());
    }

    private void writeSpacing(ConfigContext context, Writer writer) throws IOException {
        for (int i = 0; i < context.spacing(); i++)
            writer.write(System.lineSeparator());
    }

    private String extractFieldName(String line) {
        if (line == null) return null;
        line = line.trim();

        if (line.isEmpty() || line.startsWith(COMMENT_PREFIX.trim())) return null;

        final int colon = line.indexOf(':');
        if (colon == -1) return null;

        return line.substring(0, colon).trim();
    }

    @Override
    public boolean commentsSupported() {
        return true;
    }
}
