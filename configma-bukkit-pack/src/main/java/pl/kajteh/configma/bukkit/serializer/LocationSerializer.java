package pl.kajteh.configma.bukkit.serializer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import pl.kajteh.configma.serialization.ConfigSerializer;
import pl.kajteh.configma.serialization.SerializedObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LocationSerializer implements ConfigSerializer<Location> {

    @Override
    public Class<?> getTargetType() {
        return Location.class;
    }

    @Override
    public Object serialize(Location location) {
        if(location.getWorld() == null) throw new IllegalStateException("Cannot serialize location: world is null");;

        final SerializedObject serializedObject = new SerializedObject();

        serializedObject.set("world", location.getWorld().getName());
        serializedObject.set("x", location.getX());
        serializedObject.set("y", location.getY());
        serializedObject.set("z", location.getZ());

        if(location.getYaw() != 0.0f) serializedObject.set("yaw", location.getYaw());
        if(location.getPitch() != 0.0f) serializedObject.set("pitch", location.getPitch());

        return serializedObject.toMap();
    }

    @Override
    public Location deserialize(Class<Location> type, Object value) {
        final SerializedObject serializedObject = new SerializedObject(value);

        final String worldName = serializedObject.get("world");
        final World world = Optional.ofNullable(Bukkit.getWorld(worldName))
                .orElseThrow(() -> new IllegalArgumentException("World '" + worldName + "' not found."));

        return new Location(
                world,
                serializedObject.get("x"),
                serializedObject.get("y"),
                serializedObject.get("z"),
                serializedObject.get("yaw", 0.0f),
                serializedObject.get("pitch", 0.0f));
    }
}
