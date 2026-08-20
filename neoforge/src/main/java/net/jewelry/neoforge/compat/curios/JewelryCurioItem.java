package net.jewelry.neoforge.compat.curios;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.jewelry.items.JewelryItem;
import net.jewelry.util.SoundHelper;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class JewelryCurioItem extends Item implements ICurioItem, JewelryItem {
    private AttributeModifiersComponent customAttributes = AttributeModifiersComponent.builder().build();
    private final String lore;

    public JewelryCurioItem(Item.Settings settings, String lore) {
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

    @Override
    public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getAttributeModifiers(SlotContext slotContext, Identifier id, ItemStack stack) {
        // Defensive copy: the default implementation's mutability is not part of the API contract.
        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiers = LinkedHashMultimap.create();
        modifiers.putAll(ICurioItem.super.getAttributeModifiers(slotContext, id, stack));
        // `id` is already unique per equipped slot, so bonuses stack across slots. Tie the id
        // to the item as well so quickly swapping a different item within the same slot doesn't
        // reuse an id and trip vanilla's "Modifier is already applied" guard.
        var modifierId = id.withSuffixedPath("/" + Registries.ITEM.getId(stack.getItem()).getPath());
        for (var entry : this.customAttributes.modifiers()) {
            modifiers.put(entry.attribute(),
                    new EntityAttributeModifier(modifierId, entry.modifier().value(), entry.modifier().operation()));
        }
        return modifiers;
    }

    public void setConfigurableModifiers(AttributeModifiersComponent component) {
        this.customAttributes = component;
    }

    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundHelper.JEWELRY_EQUIP, 1.0F, 1.0F);
    }
}
