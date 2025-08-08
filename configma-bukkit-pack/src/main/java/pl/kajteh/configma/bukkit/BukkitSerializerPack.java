package pl.kajteh.configma.bukkit;

import pl.kajteh.configma.serialization.serializer.Serializer;
import pl.kajteh.configma.serialization.serializer.SerializerPack;

import java.util.List;

public class BukkitSerializerPack implements SerializerPack {

    @Override
    public List<Serializer<?>> getSerializers() {
        return List.of(); // todo
    }
}
