package pl.kajteh.configma.bukkitpack.serializer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.kajteh.configma.serialization.data.SerializationData;
import pl.kajteh.configma.serialization.data.SerializedData;
import pl.kajteh.configma.serialization.serializer.ObjectSerializer;

import java.util.Set;

public class ItemMetaSerializer implements ObjectSerializer<ItemMeta> {

    @Override
    public void serialize(final SerializationData data, final ItemMeta meta) {
        if(meta.hasDisplayName()) data.add("display-name", meta.getDisplayName());
        if(meta.hasLore()) data.add("lore", meta.getLore());
        if(!meta.getItemFlags().isEmpty()) data.add("flags", meta.getItemFlags());
        if(meta.hasCustomModelData()) data.add("custom-model-data", meta.getCustomModelData());
        if(meta.isUnbreakable()) data.add("unbreakable", true);
        if(meta.hasLocalizedName()) data.add("localized-name", meta.getLocalizedName());
    }

    @Override
    public ItemMeta deserialize(final SerializedData data) {
        final ItemMeta meta = new ItemStack(Material.DIAMOND).getItemMeta();

        if(meta == null) throw new IllegalStateException("Cannot create an empty ItemMeta");

        if(data.has("display-name")) meta.setDisplayName(data.get("display-name", String.class));
        if(data.has("lore")) meta.setLore(data.getAsList("lore", String.class));

        if(data.has("flags")) {
            final Set<ItemFlag> itemFlags = data.getAsSet("flags", ItemFlag.class);
            itemFlags.forEach(meta::addItemFlags);
        }

        if(data.has("custom-model-data")) meta.setCustomModelData(data.get("custom-model-data", Integer.class));
        if(data.has("unbreakable")) meta.setUnbreakable(true);
        if(data.has("localized-name")) meta.setLocalizedName(data.get("localized-name", String.class));

        return meta;
    }

    @Override
    public Class<ItemMeta> getType() {
        return ItemMeta.class;
    }
}
