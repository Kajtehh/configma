package dev.kajteh.configma.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.kajteh.configma.ConfigContext;
import dev.kajteh.configma.ConfigParser;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.function.Function;

public class JsonConfigParser implements ConfigParser {

    private final Gson gson;
    private Function<String, String> formatter = Function.identity();

    private JsonConfigParser(final Gson gson) {
        this.gson = gson;
    }

    public static JsonConfigParser standard() {
        return new JsonConfigParser(new GsonBuilder()
                .setPrettyPrinting()
                .create());
    }

    public static JsonConfigParser of(final Gson gson) {
        return new JsonConfigParser(gson);
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
    public ConfigParser withFormatter(final Function<String, String> formatter) {
        this.formatter = formatter;
        return this;
    }
}