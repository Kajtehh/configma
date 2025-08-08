package pl.kajteh.configma.bukkitpack.serializer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.kajteh.configma.serialization.data.SerializationData;
import pl.kajteh.configma.serialization.data.SerializedData;
import pl.kajteh.configma.serialization.serializer.ObjectSerializer;

public class ItemStackSerializer implements ObjectSerializer<ItemStack> {

    @Override
    public void serialize(SerializationData data, ItemStack itemStack) {
        data.add("type", itemStack.getType());

        if(itemStack.getAmount() != 1) data.add("amount", itemStack.getAmount());
        if(itemStack.hasItemMeta()) data.add("meta", itemStack.getItemMeta());
        if(!itemStack.getEnchantments().isEmpty()) data.add("enchantments", itemStack.getEnchantments());
    }

    @Override
    public ItemStack deserialize(SerializedData data) {
        if (!data.has("type")) throw new IllegalArgumentException("Missing 'type' for ItemStack");

        final Material type = data.get("type");
        final ItemStack itemStack = new ItemStack(type);

        if(data.has("amount")) itemStack.setAmount(data.get("amount"));
        if(data.has("meta")) itemStack.setItemMeta(data.get("meta"));
        if(data.has("enchantments")) itemStack.addUnsafeEnchantments(data.get("enchantments"));

        return itemStack;
    }

    @Override
    public Class<ItemStack> getType() {
        return ItemStack.class;
    }
}
