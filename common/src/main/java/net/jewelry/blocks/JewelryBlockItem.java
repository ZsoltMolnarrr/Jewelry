package net.jewelry.blocks;

import net.minecraft.block.Block;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

/// `BlockItem` carrying an optional grey/italic hint line.
///
/// 1.21.11 removed `Block#appendTooltip`, so block hints have to be appended by the block's *item*.
/// The tooltip hook itself also changed shape: it takes a `TooltipDisplayComponent` and a
/// `Consumer<Text>` instead of a `List<Text>`.
public class JewelryBlockItem extends BlockItem {
    private final String hint;

    public JewelryBlockItem(Block block, Settings settings, String hint) {
        super(block, settings);
        this.hint = hint;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent,
                              Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
        if (hint != null && !hint.isEmpty()) {
            textConsumer.accept(Text.translatable(hint).formatted(Formatting.GRAY, Formatting.ITALIC));
        }
    }
}
