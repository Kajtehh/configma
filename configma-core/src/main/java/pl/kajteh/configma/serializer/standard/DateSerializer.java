package pl.kajteh.configma.serializer.standard;

import pl.kajteh.configma.serializer.ConfigSerializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateSerializer implements ConfigSerializer<Date> {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public Class<?> getTargetType() {
        return Date.class;
    }

    @Override
    public Object serialize(Date date) {
        return formatter.format(date);
    }

    @Override
    public Date deserialize(Class<Date> type, Object value) {
        try {
            if (value instanceof String) {
                return formatter.parse((String) value);
            }
            throw new IllegalArgumentException("Cannot deserialize Date from non-string value");
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date string: " + value, e);
        }
    }
}