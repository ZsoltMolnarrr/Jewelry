package net.jewelry.fabric.compat.trinkets;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.jewelry.items.JewelryItem;
import net.jewelry.util.SoundHelper;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public class JewelryTrinketItem extends TrinketItem implements JewelryItem {
    private AttributeModifiersComponent customAttributes = AttributeModifiersComponent.builder().build();
    private final String lore;

    public JewelryTrinketItem(Settings settings, String lore) {
        super(settings);
        this.lore = lore;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (lore != null && !lore.isEmpty()) {
            tooltip.add(Text.translatable(lore).formatted(Formatting.ITALIC, Formatting.GOLD));
        }
    }

    public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier slotIdentifier) {
        var defaultModifiers = super.getModifiers(stack, slot, entity, slotIdentifier);
        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> mutableModifiers = ArrayListMultimap.create(defaultModifiers);
        String itemName = net.minecraft.registry.Registries.ITEM.getId(stack.getItem()).getPath();

        for (var entry : this.customAttributes.modifiers()) {
            Identifier uniqueModId = Identifier.of(slotIdentifier.getNamespace(),
                    slotIdentifier.getPath() + "_" + itemName + "_" + entry.modifier().id().getPath());
            mutableModifiers.put(entry.attribute(),
                    new EntityAttributeModifier(uniqueModId, entry.modifier().value(), entry.modifier().operation()));
        }
        return mutableModifiers;
    }
    public void setConfigurableModifiers(AttributeModifiersComponent component) {
        this.customAttributes = component;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onEquip(stack, slot, entity);

        if (entity.getWorld().isClient() // Play sound only on client
                && entity.age > 100      // Avoid playing sound on entering world / dimension
        ) {
            entity.playSound(SoundHelper.JEWELRY_EQUIP, 1.0F, 1.0F);
        }
    }
}