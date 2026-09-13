package net.jewelry.gems;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.util.Optional;

/// A gem cut: the recipe that turns a raw gem into a cut gem carrying one attribute bonus.
///
/// Loaded from data packs into the synced `gem_cut` registry ([GemCutRegistry]), one file per cut at
/// `data/<mod>/gem_cut/<id>.json`. A cut gem is the raw gem item with the [GemComponents#CUT] component
/// referencing one of these entries — the same shape as an enchanted book referencing an enchantment.
///
/// `attribute` is kept as an identifier rather than a registry entry on purpose: a cut whose attribute
/// comes from an absent mod still loads, still names and colours itself, and merely contributes nothing.
public record GemCut(
        RegistryEntry<Item> gem,
        Identifier attribute,
        float value,
        EntityAttributeModifier.Operation operation,
        TextColor color,
        /// Item model of the cut gem (e.g. `jewelry:item/gem_cut/bold_ruby`, a file under
        /// `models/item/gem_cut/`); absent → renders as the raw gem.
        Optional<Identifier> model
) {
    public static final Codec<GemCut> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registries.ITEM.getEntryCodec().fieldOf("gem").forGetter(GemCut::gem),
            Identifier.CODEC.fieldOf("attribute").forGetter(GemCut::attribute),
            Codec.FLOAT.fieldOf("value").forGetter(GemCut::value),
            EntityAttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(GemCut::operation),
            TextColor.CODEC.fieldOf("color").forGetter(GemCut::color),
            Identifier.CODEC.optionalFieldOf("model").forGetter(GemCut::model)
    ).apply(instance, GemCut::new));

    // MARK: Naming

    public static String translationKey(Identifier id) {
        return "gem_cut." + id.getNamespace() + "." + id.getPath();
    }

    public static Optional<Identifier> idOf(RegistryEntry<GemCut> entry) {
        return entry.getKey().map(key -> key.getValue());
    }

    /// Display name of the cut gem, e.g. "Bold Ruby". Plain text: the cut's colour is for the socket glyph only.
    public static MutableText name(RegistryEntry<GemCut> entry) {
        var key = idOf(entry).map(GemCut::translationKey).orElse("gem_cut.unknown");
        return Text.translatable(key);
    }

    // MARK: Attribute

    public Optional<RegistryEntry<EntityAttribute>> resolveAttribute() {
        return Registries.ATTRIBUTE.getEntry(attribute).map(reference -> (RegistryEntry<EntityAttribute>) reference);
    }

    /// The modifier this cut contributes under the given id. Callers choose an id unique per placement
    /// (cut, slot, socket index) so equal cuts stack instead of overwriting each other on an entity.
    public EntityAttributeModifier modifier(Identifier modifierId) {
        return new EntityAttributeModifier(modifierId, value, operation);
    }

    // MARK: Stacks

    /// A single cut gem of this cut.
    public static ItemStack stack(RegistryEntry<GemCut> entry) {
        var stack = new ItemStack(entry.value().gem());
        stack.set(GemComponents.CUT, entry);
        return stack;
    }

    public static Optional<RegistryEntry<GemCut>> of(ItemStack stack) {
        return Optional.ofNullable(stack.get(GemComponents.CUT));
    }
}
