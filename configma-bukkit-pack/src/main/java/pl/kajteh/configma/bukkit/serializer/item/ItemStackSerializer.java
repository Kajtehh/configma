package pl.kajteh.configma.bukkit.serializer.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.kajteh.configma.serialization.ConfigSerializer;
import pl.kajteh.configma.serialization.SerializedObject;

public class ItemStackSerializer implements ConfigSerializer<ItemStack> {

    @Override
    public Class<?> getTargetType() {
        return ItemStack.class;
    }

    @Override
    public Object serialize(ItemStack itemStack) {
        final SerializedObject serializedObject = new SerializedObject();

        serializedObject.set("type", itemStack.getType());

        if(itemStack.getAmount() != 1) serializedObject.set("amount", itemStack.getAmount());
        if(itemStack.hasItemMeta()) serializedObject.set("meta", itemStack.getItemMeta());
        if(!itemStack.getEnchantments().isEmpty()) serializedObject.set("enchantments", itemStack.getEnchantments());

        return serializedObject;
    }

    @Override
    public ItemStack deserialize(Class<ItemStack> clazz, Object value) {
        final SerializedObject serializedObject = new SerializedObject(value);

        final Material type = serializedObject.get("type");

        if (type == null) throw new IllegalArgumentException("Missing 'type' for ItemStack");

        final ItemStack itemStack = new ItemStack(type);

        if(serializedObject.has("amount")) {
            itemStack.setAmount(serializedObject.get("amount"));
        }

        if(serializedObject.has("meta")) {
            itemStack.setItemMeta(serializedObject.get("meta"));
        }

        if(serializedObject.has("enchantments")) {
            itemStack.addUnsafeEnchantments(serializedObject.get("enchantments"));
        }

        return itemStack;
    }
}
