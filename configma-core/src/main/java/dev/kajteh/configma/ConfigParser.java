package dev.kajteh.configma;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

public interface ConfigParser {
    Map<String, Object> load(final Reader reader);
    void write(final Writer writer, final ConfigContext context, final Map<String, Object> values);

    default String formatField(final String name) {
        return name;
    }

    default boolean commentsSupported() {
        return false;
    }
}
