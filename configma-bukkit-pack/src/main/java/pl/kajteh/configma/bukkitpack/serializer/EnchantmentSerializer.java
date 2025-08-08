package pl.kajteh.configma.bukkitpack.serializer;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import pl.kajteh.configma.serialization.serializer.ValueSerializer;

public class EnchantmentSerializer implements ValueSerializer<Enchantment> {
    @Override
    public Object serialize(Enchantment enchantment) {
        return enchantment.getKey().getKey().toUpperCase();
    }

    @Override
    public Enchantment deserialize(Object raw) {
        if (!(raw instanceof String enchantmentString)) {
            throw new IllegalArgumentException("Expected string for enchantment, got " + raw.getClass());
        }

        final NamespacedKey key = NamespacedKey.fromString(enchantmentString);

        if(key == null) throw new IllegalArgumentException("Unknown enchantment: " + raw);

        final Enchantment enchantment = Enchantment.getByKey(key);

        if(enchantment == null) throw new IllegalArgumentException("Unknown enchantment: " + raw);

        return enchantment;
    }

    @Override
    public Class<Enchantment> getType() {
        return Enchantment.class;
    }
}
