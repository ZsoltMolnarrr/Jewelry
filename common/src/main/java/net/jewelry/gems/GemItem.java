package net.jewelry.gems;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

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

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        GemCut.of(stack).ifPresent(entry -> {
            tooltip.add(entry.value().bonus().description());
            if (GemSockets.enabled()) {
                tooltip.add(Text.translatable(CUT_HINT).formatted(Formatting.GRAY));
            }
        });
    }

}
