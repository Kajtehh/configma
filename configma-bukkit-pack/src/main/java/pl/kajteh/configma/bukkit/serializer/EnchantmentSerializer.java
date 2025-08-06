package pl.kajteh.configma.bukkit.serializer;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import pl.kajteh.configma.serialization.ConfigSerializer;

public class EnchantmentSerializer implements ConfigSerializer<Enchantment> {

    @Override
    public Class<?> getTargetType() {
        return Enchantment.class;
    }

    @Override
    public Object serialize(Enchantment enchantment) {
        return enchantment.getKey().getKey().toUpperCase();
    }

    @Override
    public Enchantment deserialize(Class<Enchantment> type, Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Expected string for enchantment, got " + value.getClass());
        }
        final String string = (String) value;

        final NamespacedKey key = NamespacedKey.fromString(string);

        if(key == null) throw new IllegalArgumentException("Unknown enchantment: " + value);

        final Enchantment enchantment = Enchantment.getByKey(key);

        if (enchantment == null) throw new IllegalArgumentException("Unknown enchantment: " + value);

        return enchantment;
    }
}
