package net.jewelry.gems;

import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/// A gem: raw when it carries no [GemComponents#CUT] component, cut otherwise.
/// A cut gem takes its name and colour from the cut and lists the cut's bonus in its tooltip.
public class GemItem extends Item {
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
        GemCut.of(stack).ifPresent(entry -> tooltip.add(bonusText(entry.value())));
    }

    /// "+5% Attack Damage" in the same wording vanilla uses for positive item attribute modifiers.
    public static Text bonusText(GemCut cut) {
        var attribute = cut.resolveAttribute();
        if (attribute.isEmpty()) {
            return Text.literal(cut.attribute().toString()).formatted(Formatting.DARK_GRAY);
        }
        var operation = cut.operation();
        double value = cut.value();
        double shown = (operation == EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                || operation == EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                ? value * 100.0
                : value;
        var prefix = value < 0 ? "attribute.modifier.take." : "attribute.modifier.plus.";
        return Text.translatable(
                        prefix + operation.getId(),
                        AttributeModifiersComponent.DECIMAL_FORMAT.format(Math.abs(shown)),
                        Text.translatable(attribute.get().value().getTranslationKey()))
                .formatted(value < 0 ? Formatting.RED : Formatting.BLUE);
    }
}
