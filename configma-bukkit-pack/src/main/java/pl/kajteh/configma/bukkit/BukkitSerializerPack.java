package pl.kajteh.configma.bukkit;

import pl.kajteh.configma.bukkit.serializer.*;
import pl.kajteh.configma.bukkit.serializer.item.ItemMetaSerializer;
import pl.kajteh.configma.bukkit.serializer.item.ItemStackSerializer;
import pl.kajteh.configma.serialization.ConfigSerializer;
import pl.kajteh.configma.serialization.pack.ConfigSerializerPack;

import java.util.List;

public class BukkitSerializerPack implements ConfigSerializerPack {

    @Override
    public List<ConfigSerializer<?>> getSerializers() {
        return List.of(
                new ColorSerializer(),
                new EnchantmentSerializer(),
                new ItemMetaSerializer(),
                new ItemStackSerializer(),
                new LocationSerializer(),
                new PotionEffectSerializer()
        );
    }
}
