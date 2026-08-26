package net.jewelry.items;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class VanillaJewelryItem extends Item {
    private final String lore;

    public VanillaJewelryItem(Properties settings, String lore) {
        super(settings);
        this.lore = lore;
    }

    // 1.21.6+: `appendTooltip` takes a `TooltipDisplayComponent` and a `Consumer<Text>` sink.
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent,
                              Consumer<Component> textConsumer, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
        if (lore != null && !lore.isEmpty()) {
            textConsumer.accept(Component.translatable(lore).withStyle(ChatFormatting.ITALIC, ChatFormatting.GOLD));
        }
    }
}
