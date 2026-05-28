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
import java.util.Map;

public class JewelryTrinketItem extends TrinketItem implements JewelryItem {
    // PHỤC HỒI LẠI BIẾN CỦA TÁC GIẢ
    private AttributeModifiersComponent customAttributes = AttributeModifiersComponent.builder().build();
    private final String lore;

    public JewelryTrinketItem(Settings settings, String lore) {
        super(settings);
        this.lore = lore;
    }

    // PHỤC HỒI LẠI HÀM CỦA TÁC GIẢ ĐỂ KHÔNG BỊ LỖI BÊN TRINKETSHELPER
    public void setConfigurableModifiers(AttributeModifiersComponent component) {
        this.customAttributes = component;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (lore != null && !lore.isEmpty()) {
            tooltip.add(Text.translatable(lore).formatted(Formatting.ITALIC, Formatting.GOLD));
        }
    }

    @Override
    public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier slotIdentifier) {
        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> uniqueModifiers = ArrayListMultimap.create();

        try {
            Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> defaultModifiers = super.getModifiers(stack, slot, entity, slotIdentifier);
            String itemName = net.minecraft.registry.Registries.ITEM.getId(stack.getItem()).getPath();

            for (Map.Entry<RegistryEntry<EntityAttribute>, EntityAttributeModifier> entry : defaultModifiers.entries()) {
                EntityAttributeModifier modifier = entry.getValue();
                String attributeName = entry.getKey().value().getTranslationKey().replace("attribute.name.", "");

                String rawPath = slotIdentifier.getPath() + "_" + itemName + "_base_" + attributeName + "_" + modifier.operation().name();
                String safePath = rawPath.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9/._-]", "_");

                uniqueModifiers.put(entry.getKey(), new EntityAttributeModifier(Identifier.of(slotIdentifier.getNamespace(), safePath), modifier.value(), modifier.operation()));
            }
            for (AttributeModifiersComponent.Entry entry : this.customAttributes.modifiers()) {
                EntityAttributeModifier modifier = entry.modifier();
                String attributeName = entry.attribute().value().getTranslationKey().replace("attribute.name.", "");

                String rawPath = slotIdentifier.getPath() + "_" + itemName + "_" + attributeName + "_" + modifier.operation().name();
                String safePath = rawPath.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9/._-]", "_");

                Identifier uniqueModId = Identifier.of(slotIdentifier.getNamespace(), safePath);

                uniqueModifiers.put(entry.attribute(),
                        new EntityAttributeModifier(uniqueModId, modifier.value(), modifier.operation()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return uniqueModifiers;
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