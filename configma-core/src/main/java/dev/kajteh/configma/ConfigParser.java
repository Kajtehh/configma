package dev.kajteh.configma;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.function.Function;

public interface ConfigParser {
    Map<String, Object> load(final Reader reader);
    void write(final Writer writer, final Map<String, Object> values, final ConfigContext context);
    Function<String, String> formatter();
    ConfigParser withFormatter(final Function<String, String> formatter);

    default boolean commentsSupported() {
        return false;
    }
}
