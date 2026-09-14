package net.jewelry.gems;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/// An item that adds sockets to equipment at an anvil; what it fits and how much it adds is its
/// [GemComponents#SOCKET_MOUNT] component. The tooltip hint is the lang key `<item key>.hint`.
public class SocketMountItem extends Item {
    public SocketMountItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable(this.getTranslationKey() + ".hint").formatted(Formatting.GRAY));
    }
}
