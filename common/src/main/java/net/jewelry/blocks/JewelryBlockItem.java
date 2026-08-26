package net.jewelry.blocks;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

/// `BlockItem` carrying an optional grey/italic hint line.
///
/// 1.21.11 removed `Block#appendTooltip`, so block hints have to be appended by the block's *item*.
/// The tooltip hook itself also changed shape: it takes a `TooltipDisplayComponent` and a
/// `Consumer<Text>` instead of a `List<Text>`.
public class JewelryBlockItem extends BlockItem {
    private final String hint;

    public JewelryBlockItem(Block block, Properties settings, String hint) {
        super(block, settings);
        this.hint = hint;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent,
                              Consumer<Component> textConsumer, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
        if (hint != null && !hint.isEmpty()) {
            textConsumer.accept(Component.translatable(hint).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }
}
