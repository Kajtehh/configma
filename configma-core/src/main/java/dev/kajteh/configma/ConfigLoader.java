package dev.kajteh.configma;

import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.function.Function;

public interface ConfigLoader {

    Map<String, Object> load(final Reader reader, final ConfigContext context);

    void write(final Writer writer, final Map<String, Object> values, final ConfigContext context);

    @NotNull String fileExtension();
    
    default @NotNull String formatField(@NotNull String name) {
        return name;
    };

    default boolean commentsSupported() {
        return false;
    }
}
