package dev.kajteh.configma.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.kajteh.configma.ConfigContext;
import dev.kajteh.configma.ConfigLoader;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.function.Function;

public class JsonConfigLoader implements ConfigLoader {

    private final Gson gson;
    private Function<String, String> formatter = Function.identity();

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
    public Function<String, String> formatter() {
        return this.formatter;
    }

    @Override
    public ConfigLoader withFormatter(final Function<String, String> formatter) {
        this.formatter = formatter;
        return this;
    }
}