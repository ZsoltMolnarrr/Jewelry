package net.jewelry.gems;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

/// An item that adds sockets to equipment at an anvil; what it fits and how much it adds is its
/// [GemComponents#SOCKET_MOUNT] component. The tooltip hint is the lang key `<item key>.hint`.
public class SocketMountItem extends Item {
    public SocketMountItem(Settings settings) {
        super(settings);
    }

    // 1.21.6+: `appendTooltip` takes a `TooltipDisplayComponent` and a `Consumer<Text>` sink.
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent,
                              Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
        textConsumer.accept(Text.translatable(this.getTranslationKey() + ".hint").formatted(Formatting.GRAY));
    }
}
