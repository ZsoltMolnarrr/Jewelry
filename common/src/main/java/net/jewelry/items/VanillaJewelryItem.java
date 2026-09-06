package net.jewelry.items;

import com.google.common.collect.Multimap;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// Fallback jewelry item used when neither Trinkets (Fabric) nor Curios (Forge) is installed.
///
/// **Port sacrifice:** on 1.21 the bonuses were an `AttributeModifiersComponent` entry with
/// `AttributeModifierSlot.ANY`, which applies in every slot from a single tooltip group. 1.20.1 has no
/// "any slot" group — `Item#getAttributeModifiers` is asked one `EquipmentSlot` at a time and vanilla
/// prints one tooltip block per answering slot — so the fallback answers for the main hand only. That
/// is the only slot a plain item can actually be equipped into anyway; with a slot mod installed this
/// class is never constructed (see `JewelryFactory`).
public class VanillaJewelryItem extends Item implements JewelryItem {
    private final JewelryModifiers modifiers;
    private final String lore;

    public VanillaJewelryItem(Settings settings, JewelryModifiers modifiers, String lore) {
        super(settings);
        this.modifiers = modifiers != null ? modifiers : JewelryModifiers.EMPTY;
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
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return modifiers.multimap();
        }
        return super.getAttributeModifiers(slot);
    }
}
