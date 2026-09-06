package net.jewelry.fabric.compat.trinkets;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.jewelry.items.JewelryItem;
import net.jewelry.items.JewelryModifiers;
import net.jewelry.util.SoundHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class JewelryTrinketItem extends TrinketItem implements JewelryItem {
    private JewelryModifiers customAttributes = JewelryModifiers.EMPTY;
    private final String lore;

    public JewelryTrinketItem(Settings settings, String lore) {
        super(settings);
        this.lore = lore;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (lore != null && !lore.isEmpty()) {
            tooltip.add(Text.translatable(lore).formatted(Formatting.ITALIC, Formatting.GOLD));
        }
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot,
                                                                          LivingEntity entity, UUID uuid) {
        var modifiers = super.getModifiers(stack, slot, entity, uuid);
        // `uuid` is already unique per equipped slot, so bonuses stack across slots; JewelryModifiers
        // folds the per-item modifier id into it so two attributes of one piece stay distinct and the
        // derivation stays deterministic (which is what lets Trinkets remove them again on unequip).
        modifiers.putAll(this.customAttributes.multimap(uuid));
        return modifiers;
    }

    public void setConfigurableModifiers(JewelryModifiers modifiers) {
        this.customAttributes = modifiers != null ? modifiers : JewelryModifiers.EMPTY;
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
