package pl.kajteh.configma.bukkitpack;

import pl.kajteh.configma.bukkitpack.serializer.EnchantmentSerializer;
import pl.kajteh.configma.bukkitpack.serializer.ItemMetaSerializer;
import pl.kajteh.configma.bukkitpack.serializer.ItemStackSerializer;
import pl.kajteh.configma.serialization.serializer.Serializer;
import pl.kajteh.configma.serialization.serializer.SerializerPack;

import java.util.List;

public class BukkitSerializerPack implements SerializerPack {

    @Override
    public List<Serializer<?>> getSerializers() {
        return List.of(
                new EnchantmentSerializer(),
                new ItemMetaSerializer(),
                new ItemStackSerializer()
        );
    }
}
