package pl.kajteh.configma.bukkitpack.serializer;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.kajteh.configma.serialization.data.SerializationData;
import pl.kajteh.configma.serialization.data.SerializedData;
import pl.kajteh.configma.serialization.serializer.ObjectSerializer;

import java.util.Objects;

public class ItemStackSerializer implements ObjectSerializer<ItemStack> {

    @Override
    public void serialize(SerializationData data, ItemStack itemStack) {
        data.add("type", itemStack.getType());

        if(itemStack.getAmount() != 1) data.add("amount", itemStack.getAmount());
        if(itemStack.hasItemMeta()) data.add("meta", Objects.requireNonNull(itemStack.getItemMeta()), ItemMeta.class);
        if(!itemStack.getEnchantments().isEmpty()) data.add("enchantments", itemStack.getEnchantments(), Enchantment.class);
    }

    @Override
    public ItemStack deserialize(SerializedData data) {
        if (!data.has("type")) throw new IllegalArgumentException("Missing 'type' for ItemStack");

        final Material type = data.get("type", Material.class);
        final ItemStack itemStack = new ItemStack(type);

        if(data.has("amount")) itemStack.setAmount(data.get("amount", Integer.class));
        if(data.has("meta")) itemStack.setItemMeta(data.get("meta", ItemMeta.class));
        if(data.has("enchantments")) itemStack.addUnsafeEnchantments(data.getAsMap("enchantments", Enchantment.class, Integer.class));

        return itemStack;
    }

    @Override
    public Class<ItemStack> getType() {
        return ItemStack.class;
    }
}
