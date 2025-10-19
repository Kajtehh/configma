package dev.kajteh.configma.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.kajteh.configma.ConfigParser;
import dev.kajteh.configma.ConfigNamingStyle;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

public class JsonConfigParser implements ConfigParser {

    private final Gson gson;

    public JsonConfigParser(final Gson gson) {
        this.gson = gson;
    }

    public JsonConfigParser() {
        this(new GsonBuilder()
                .setPrettyPrinting()
                .create());
    }

    @Override
    public Map<String, Object> load(final Reader reader) {
        return this.gson.fromJson(reader, new TypeToken<Map<String, Object>>() {}.getType());
    }

    @Override
    public void write(final Writer writer, final Map<String, Object> values) {
        this.gson.toJson(values, writer);
    }

    @Override
    public ConfigNamingStyle getNamingStyle() {
        return ConfigNamingStyle.CAMEL;
    }
}