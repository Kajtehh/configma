package dev.kajteh.configma.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.kajteh.configma.ConfigContext;
import dev.kajteh.configma.ConfigLoader;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

public class JsonConfigLoader implements ConfigLoader {

    private final Gson gson;

    private JsonConfigLoader(final Gson gson) {
        this.gson = gson;
    }

    public static JsonConfigLoader createDefault() {
        return new JsonConfigLoader(new GsonBuilder()
                .setPrettyPrinting()
                .create());
    }

    public static JsonConfigLoader create(final Gson gson) {
        return new JsonConfigLoader(gson);
    }

    @Override
    public Map<String, Object> load(final Reader reader, final ConfigContext context) {
        return this.gson.fromJson(reader, new TypeToken<Map<String, Object>>() {}.getType());
    }

    @Override
    public void write(final Writer writer, final Map<String, Object> values, final ConfigContext context) {
        this.gson.toJson(values, writer);
    }

    @Override
    public @NotNull String fileExtension() {
        return "json";
    }
}