package net.jewelry.internals;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.UUID;

public class RingItem extends TrinketItem {
    private List<Modifier> configurableModifiers = List.of();

    public RingItem(Settings settings) {
        super(settings.maxCount(1));
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var modifiers = super.getModifiers(stack, slot, entity, uuid);
        for (var modifier : this.configurableModifiers) {
            modifiers.put(modifier.attribute,
                    new EntityAttributeModifier(uuid, modifier.name, modifier.value, modifier.operation));
        }
        return modifiers;
    }

    public record Modifier(EntityAttribute attribute, String name, float value, EntityAttributeModifier.Operation operation) { }

    public void setConfigurableModifiers(List<Modifier> configurableModifiers) {
        this.configurableModifiers = configurableModifiers;
    }
}
