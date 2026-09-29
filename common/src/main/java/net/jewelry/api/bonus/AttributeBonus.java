package net.jewelry.api.bonus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Optional;

/// The bonus kind Jewelry ships: one attribute modifier, applied by Jewelry while the socketed item is
/// equipped in a slot it targets (see `GemSockets.applyModifiers`).
///
/// `attribute` is kept as an identifier rather than a registry entry on purpose: a cut whose attribute
/// comes from an absent mod still loads, still names and colours itself, and merely contributes nothing.
public record AttributeBonus(Identifier attribute, float value, EntityAttributeModifier.Operation operation) implements GemBonus {
    public static final MapCodec<AttributeBonus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("attribute").forGetter(AttributeBonus::attribute),
            Codec.FLOAT.fieldOf("value").forGetter(AttributeBonus::value),
            EntityAttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeBonus::operation)
    ).apply(instance, AttributeBonus::new));

    @Override
    public GemBonusType<?> type() {
        return GemBonusTypes.ATTRIBUTE;
    }

    public Optional<RegistryEntry<EntityAttribute>> resolveAttribute() {
        return Registries.ATTRIBUTE.getEntry(attribute).map(reference -> (RegistryEntry<EntityAttribute>) reference);
    }

    /// The modifier this bonus contributes under the given id. Callers choose an id unique per placement
    /// (cut, slot, socket index) so equal cuts stack instead of overwriting each other on an entity.
    public EntityAttributeModifier modifier(Identifier modifierId) {
        return new EntityAttributeModifier(modifierId, value, operation);
    }

    /// "+5% Attack Damage" in the same wording vanilla uses for positive item attribute modifiers; the bare
    /// attribute id in dark gray when the attribute is not present in this game.
    @Override
    public Text description() {
        var attribute = resolveAttribute();
        if (attribute.isEmpty()) {
            return Text.literal(this.attribute.toString()).formatted(Formatting.DARK_GRAY);
        }
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
