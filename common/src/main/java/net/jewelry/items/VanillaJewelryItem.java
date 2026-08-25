package net.jewelry.items;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public class VanillaJewelryItem extends Item {
    private final String lore;

    public VanillaJewelryItem(Settings settings, String lore) {
        super(settings);
        this.lore = lore;
    }

    // 1.21.6+: `appendTooltip` takes a `TooltipDisplayComponent` and a `Consumer<Text>` sink.
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent,
                              Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
        if (lore != null && !lore.isEmpty()) {
            textConsumer.accept(Text.translatable(lore).formatted(Formatting.ITALIC, Formatting.GOLD));
        }
    }
}
