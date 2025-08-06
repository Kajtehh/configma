package pl.kajteh.configma.bukkit.serializer;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.kajteh.configma.serialization.ConfigSerializer;
import pl.kajteh.configma.serialization.SerializedObject;

public class PotionEffectSerializer implements ConfigSerializer<PotionEffect> {

    @Override
    public Class<?> getTargetType() {
        return PotionEffect.class;
    }

    @Override
    public Object serialize(PotionEffect potionEffect) {
        final SerializedObject serializedObject = new SerializedObject();

        serializedObject.set("type", potionEffect.getType().getName());
        serializedObject.set("duration", potionEffect.getDuration());
        serializedObject.set("amplifier", potionEffect.getAmplifier());

        if(potionEffect.isAmbient()) serializedObject.set("ambient", true);
        if(potionEffect.hasParticles()) serializedObject.set("particles", true);
        if(potionEffect.hasIcon()) serializedObject.set("icon", true);

        return serializedObject.toMap();
    }

    @Override
    public PotionEffect deserialize(Class<PotionEffect> type, Object value) {
        final SerializedObject serializedObject = new SerializedObject(value);

        final PotionEffectType effectType = PotionEffectType.getByName(serializedObject.get("type"));

        if (effectType == null)
            throw new IllegalArgumentException("Invalid potion effect type: " + serializedObject.get("type"));

        return new PotionEffect(
                effectType,
                serializedObject.get("duration", 20),
                serializedObject.get("amplifier", 0),
                serializedObject.has("ambient"),
                serializedObject.has("particles"),
                serializedObject.has("icon")
        );
    }
}
