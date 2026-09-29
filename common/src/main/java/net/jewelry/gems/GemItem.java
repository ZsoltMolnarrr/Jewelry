package net.jewelry.gems;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

/// A gem: raw when it carries no [GemComponents#CUT] component, cut otherwise.
/// A cut gem takes its name and colour from the cut and lists the cut's bonus in its tooltip, followed by a
/// hint to socket it.
public class GemItem extends Item {
    /// Usage hint of a cut gem, only while sockets are enabled.
    public static final String CUT_HINT = "item.jewelry.gem.cut.hint";

    public GemItem(Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        return GemCut.of(stack)
                .map(entry -> (Text) GemCut.name(entry))
                .orElseGet(() -> super.getName(stack));
    }

    // 1.21.6+: `appendTooltip` takes a `TooltipDisplayComponent` and a `Consumer<Text>` sink.
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent,
                              Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
        GemCut.of(stack).ifPresent(entry -> {
            textConsumer.accept(entry.value().bonus().description());
            if (GemSockets.enabled()) {
                textConsumer.accept(Text.translatable(CUT_HINT).formatted(Formatting.GRAY));
            }
        });
    }

}
