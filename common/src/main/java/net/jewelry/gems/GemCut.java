package net.jewelry.gems;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.jewelry.api.bonus.GemBonus;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.util.Optional;

/// A gem cut: the recipe that turns a raw gem into a cut gem carrying one bonus.
///
/// Loaded from data packs into the synced `gem_cut` registry ([GemCutRegistry]), one file per cut at
/// `data/<mod>/gem_cut/<id>.json`. A cut gem is the raw gem item with the [GemComponents#CUT] component
/// referencing one of these entries — the same shape as an enchanted book referencing an enchantment.
///
/// `bonus` is whatever the cut grants — Jewelry's cuts carry an [net.jewelry.api.bonus.AttributeBonus];
/// mods may register further kinds (see [net.jewelry.api.bonus.GemBonusTypes]).
public record GemCut(
        RegistryEntry<Item> gem,
        GemBonus bonus,
        TextColor color,
        /// Custom item model of the cut gem (opt-in, e.g. `jewelry:item/gem_cut/bold_ruby`, a file under
        /// `models/item/gem_cut/`). Absent → the gem's default cut model ([#defaultModelId]), which every gem
        /// ships; if even that is missing, the raw gem's own model.
        Optional<Identifier> model
) {
    public static final Codec<GemCut> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registries.ITEM.getEntryCodec().fieldOf("gem").forGetter(GemCut::gem),
            GemBonus.CODEC.fieldOf("bonus").forGetter(GemCut::bonus),
            TextColor.CODEC.fieldOf("color").forGetter(GemCut::color),
            Identifier.CODEC.optionalFieldOf("model").forGetter(GemCut::model)
    ).apply(instance, GemCut::new));

    /// Model id of a cut's custom icon: `<ns>:item/gem_cut/<cut path>`, a file under `models/item/gem_cut/`.
    public static Identifier conventionalModelId(Identifier cutId) {
        return Identifier.of(cutId.getNamespace(), "item/gem_cut/" + cutId.getPath());
    }

    /// Model id of a gem's default cut look, shared by all its cuts without a custom icon:
    /// `<gem ns>:item/gem_cut/<gem path>` (e.g. `jewelry:item/gem_cut/ruby`).
    public static Identifier defaultModelId(Item gem) {
        var gemId = Registries.ITEM.getId(gem);
        return Identifier.of(gemId.getNamespace(), "item/gem_cut/" + gemId.getPath());
    }

    /// The item-model definition of a model id (1.21.4+): the same id without the `item/` folder —
    /// `jewelry:item/gem_cut/bold_ruby` → `jewelry:gem_cut/bold_ruby`, i.e. `assets/jewelry/items/gem_cut/bold_ruby.json`.
    public static Identifier definitionId(Identifier modelId) {
        var path = modelId.getPath();
        return path.startsWith("item/") ? Identifier.of(modelId.getNamespace(), path.substring("item/".length())) : modelId;
    }

    /// The model this cut gem renders with: its custom icon if opted in, else its gem's default cut model.
    public Identifier effectiveModelId() {
        return model.orElseGet(() -> defaultModelId(gem.value()));
    }

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
