package net.jewelry.internals;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotAttributes;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.jewelry.api.AttributeResolver;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RingItem extends TrinketItem {
    private Map<EntityAttribute, EntityAttributeModifier> modifiers = Map.of();

    public RingItem(Settings settings) {
        super(settings.maxCount(1));
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var modifiers = super.getModifiers(stack, slot, entity, uuid);
        for (var entry : this.modifiers.entrySet()) {
            var modifier = entry.getValue();
            modifiers.put(entry.getKey(),
                    new EntityAttributeModifier(uuid, modifier.getName(), modifier.getValue(), modifier.getOperation()));
        }
        return modifiers;
    }

    public void setModifiers(Map<EntityAttribute, EntityAttributeModifier> modifiers) {
        this.modifiers = modifiers;
    }
}
