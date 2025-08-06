package pl.kajteh.configma.bukkit.serializer.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.kajteh.configma.serialization.ConfigSerializer;
import pl.kajteh.configma.serialization.SerializedObject;

import java.util.List;

public class ItemMetaSerializer implements ConfigSerializer<ItemMeta> {

    @Override
    public Class<?> getTargetType() {
        return ItemMeta.class;
    }

    @Override
    public Object serialize(ItemMeta meta) {
        final SerializedObject serializedObject = new SerializedObject();

        if(meta.hasDisplayName()) serializedObject.set("display-name", meta.getDisplayName());
        if(meta.hasLore()) serializedObject.set("lore", meta.getLore());
        if(!meta.getItemFlags().isEmpty()) serializedObject.set("flags", List.of(meta.getItemFlags()));
        if(meta.hasCustomModelData()) serializedObject.set("custom-model-data", meta.getCustomModelData());
        if(meta.isUnbreakable()) serializedObject.set("unbreakable", true);
        if(meta.hasLocalizedName()) serializedObject.set("localized-name", meta.getLocalizedName());

        return serializedObject.toMap();
    }

    @Override
    public ItemMeta deserialize(Class<ItemMeta> type, Object value) {
        final SerializedObject serializedObject = new SerializedObject(value);

        final ItemMeta meta = new ItemStack(Material.DIAMOND).getItemMeta();

        if(meta == null) throw new IllegalStateException("Cannot create an empty ItemMeta");

        if(serializedObject.has("display-name")) meta.setDisplayName(serializedObject.get("display-name"));
        if(serializedObject.has("lore")) meta.setLore(serializedObject.get("lore"));

        if(serializedObject.has("flags")) {
            final List<ItemFlag> itemFlags = serializedObject.get("flags");
            itemFlags.forEach(meta::addItemFlags);
        }

        if(serializedObject.has("custom-model-data")) meta.setCustomModelData(serializedObject.get("custom-model-data"));
        if(serializedObject.has("unbreakable")) meta.setUnbreakable(serializedObject.get("unbreakable"));
        if(serializedObject.has("localized-name")) meta.setLocalizedName(serializedObject.get("localized-name"));

        return meta;
    }
}
