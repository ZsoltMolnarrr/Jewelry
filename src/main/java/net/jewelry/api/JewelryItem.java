package net.jewelry.api;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.jewelry.util.SoundHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class JewelryItem extends TrinketItem {
    private List<Modifier> configurableModifiers = List.of();
    private final String lore;

    public JewelryItem(Settings settings, String lore) {
        super(settings.maxCount(1));
        this.lore = lore;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (lore != null && !lore.isEmpty()) {
            tooltip.add(Text.translatable(lore).formatted(Formatting.ITALIC, Formatting.GOLD));
        }
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