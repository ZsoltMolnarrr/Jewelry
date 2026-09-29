package net.jewelry.api.bonus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/// The bonus kind Jewelry ships: one attribute modifier, applied by Jewelry while the socketed item is
/// equipped in a slot it targets (see `GemSockets.applyModifiers`).
///
/// `attribute` is kept as an identifier rather than a registry entry on purpose: a cut whose attribute
/// comes from an absent mod still loads, still names and colours itself, and merely contributes nothing.
public record AttributeBonus(Identifier attribute, float value, AttributeModifier.Operation operation) implements GemBonus {
    public static final MapCodec<AttributeBonus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("attribute").forGetter(AttributeBonus::attribute),
            Codec.FLOAT.fieldOf("value").forGetter(AttributeBonus::value),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeBonus::operation)
    ).apply(instance, AttributeBonus::new));

    @Override
    public GemBonusType<?> type() {
        return GemBonusTypes.ATTRIBUTE;
    }

    public Optional<Holder<Attribute>> resolveAttribute() {
        return BuiltInRegistries.ATTRIBUTE.get(attribute).map(reference -> (Holder<Attribute>) reference);
    }

    /// The modifier this bonus contributes under the given id. Callers choose an id unique per placement
    /// (cut, slot, socket index) so equal cuts stack instead of overwriting each other on an entity.
    public AttributeModifier modifier(Identifier modifierId) {
        return new AttributeModifier(modifierId, value, operation);
    }

    /// "+5% Attack Damage" in the same wording vanilla uses for positive item attribute modifiers; the bare
    /// attribute id in dark gray when the attribute is not present in this game.
    @Override
    public Component description() {
        var attribute = resolveAttribute();
        if (attribute.isEmpty()) {
            return Component.literal(this.attribute.toString()).withStyle(ChatFormatting.DARK_GRAY);
        }
        double shown = (operation == AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                || operation == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                ? value * 100.0
                : value;
        var prefix = value < 0 ? "attribute.modifier.take." : "attribute.modifier.plus.";
        return Component.translatable(
                        prefix + operation.id(),
                        ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(shown)),
                        Component.translatable(attribute.get().value().getDescriptionId()))
                .withStyle(value < 0 ? ChatFormatting.RED : ChatFormatting.BLUE);
    }
}
