package dev.kajteh.configma;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.function.Function;

public interface ConfigLoader {

    Map<String, Object> load(final Reader reader, final ConfigContext context);

    void write(final Writer writer, final Map<String, Object> values, final ConfigContext context);

    //Set<String> extensions(); TODO: 12/3/2025
    
    Function<String, String> formatter();
    
    ConfigLoader withFormatter(final Function<String, String> formatter);

    default boolean commentsSupported() {
        return false;
    }
}
