package dev.kajteh.configma;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

public interface ConfigParser {
    Map<String, Object> load(final Reader reader);
    void write(final Writer writer, final Map<String, Object> values, final ConfigContext context);
    ConfigFormatter formatter();
    ConfigParser withFormatter(final ConfigFormatter formatter);

    default boolean commentsSupported() {
        return false;
    }
}
