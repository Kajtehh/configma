package pl.kajteh.configma.bukkit.serializer;

import org.bukkit.Color;
import pl.kajteh.configma.serialization.ConfigSerializer;
import pl.kajteh.configma.serialization.SerializedObject;

import java.util.Map;

public class ColorSerializer implements ConfigSerializer<Color> {

    @Override
    public Class<?> getTargetType() {
        return Color.class;
    }

    @Override
    public Object serialize(Color color) {
        return Map.of(
                "r", color.getRed(),
                "g", color.getGreen(),
                "b", color.getBlue()
        );
    }

    @Override
    public Color deserialize(Class<Color> type, Object value) {
        final SerializedObject serializedObject = new SerializedObject(value);

        return Color.fromRGB(
                serializedObject.get("r", 0),
                serializedObject.get("g", 0),
                serializedObject.get("b", 0));
    }
}
