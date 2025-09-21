package net.jewelry.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class VanillaJewelryItem extends Item {
    private final String lore;

    public VanillaJewelryItem(Settings settings, String lore) {
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
}