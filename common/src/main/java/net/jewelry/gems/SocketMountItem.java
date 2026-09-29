package net.jewelry.gems;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/// An item that adds sockets to equipment at an anvil; what it fits and how much it adds is its
/// [GemComponents#SOCKET_MOUNT] component. The tooltip hint is the lang key `<item key>.hint`.
public class SocketMountItem extends Item {
    public SocketMountItem(Properties settings) {
        super(settings);
    }

    // 1.21.6+: `appendTooltip` takes a `TooltipDisplayComponent` and a `Consumer<Text>` sink.
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent,
                              Consumer<Component> textConsumer, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
        textConsumer.accept(Component.translatable(this.getDescriptionId() + ".hint").withStyle(ChatFormatting.GRAY));
    }
}
