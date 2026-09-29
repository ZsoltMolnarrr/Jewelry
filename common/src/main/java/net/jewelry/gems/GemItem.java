package net.jewelry.gems;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/// A gem: raw when it carries no [GemComponents#CUT] component, cut otherwise.
/// A cut gem takes its name and colour from the cut and lists the cut's bonus in its tooltip, followed by a
/// hint to socket it.
public class GemItem extends Item {
    /// Usage hint of a cut gem, only while sockets are enabled.
    public static final String CUT_HINT = "item.jewelry.gem.cut.hint";

    public GemItem(Properties settings) {
        super(settings);
    }

    @Override
    public Component getName(ItemStack stack) {
        return GemCut.of(stack)
                .map(entry -> (Component) GemCut.name(entry))
                .orElseGet(() -> super.getName(stack));
    }

    // 1.21.6+: `appendTooltip` takes a `TooltipDisplayComponent` and a `Consumer<Text>` sink.
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent,
                              Consumer<Component> textConsumer, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
        GemCut.of(stack).ifPresent(entry -> {
            textConsumer.accept(entry.value().bonus().description());
            if (GemSockets.enabled()) {
                textConsumer.accept(Component.translatable(CUT_HINT).withStyle(ChatFormatting.GRAY));
            }
        });
    }

}
