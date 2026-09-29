package net.jewelry.gems;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.jewelry.api.bonus.GemBonus;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
        Holder<Item> gem,
        GemBonus bonus,
        TextColor color,
        /// Custom item model of the cut gem (opt-in, e.g. `jewelry:item/gem_cut/bold_ruby`, a file under
        /// `models/item/gem_cut/`). Absent → the gem's default cut model ([#defaultModelId]), which every gem
        /// ships; if even that is missing, the raw gem's own model.
        Optional<Identifier> model
) {
    public static final Codec<GemCut> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("gem").forGetter(GemCut::gem),
            GemBonus.CODEC.fieldOf("bonus").forGetter(GemCut::bonus),
            TextColor.CODEC.fieldOf("color").forGetter(GemCut::color),
            Identifier.CODEC.optionalFieldOf("model").forGetter(GemCut::model)
    ).apply(instance, GemCut::new));

    /// Model id of a cut's custom icon: `<ns>:item/gem_cut/<cut path>`, a file under `models/item/gem_cut/`.
    public static Identifier conventionalModelId(Identifier cutId) {
        return Identifier.fromNamespaceAndPath(cutId.getNamespace(), "item/gem_cut/" + cutId.getPath());
    }

    /// Model id of a gem's default cut look, shared by all its cuts without a custom icon:
    /// `<gem ns>:item/gem_cut/<gem path>` (e.g. `jewelry:item/gem_cut/ruby`).
    public static Identifier defaultModelId(Item gem) {
        var gemId = BuiltInRegistries.ITEM.getKey(gem);
        return Identifier.fromNamespaceAndPath(gemId.getNamespace(), "item/gem_cut/" + gemId.getPath());
    }

    /// The item-model definition of a model id (1.21.4+): the same id without the `item/` folder —
    /// `jewelry:item/gem_cut/bold_ruby` → `jewelry:gem_cut/bold_ruby`, i.e. `assets/jewelry/items/gem_cut/bold_ruby.json`.
    public static Identifier definitionId(Identifier modelId) {
        var path = modelId.getPath();
        return path.startsWith("item/") ? Identifier.fromNamespaceAndPath(modelId.getNamespace(), path.substring("item/".length())) : modelId;
    }

    /// The model this cut gem renders with: its custom icon if opted in, else its gem's default cut model.
    public Identifier effectiveModelId() {
        return model.orElseGet(() -> defaultModelId(gem.value()));
    }

    // MARK: Naming

    public static String translationKey(Identifier id) {
        return "gem_cut." + id.getNamespace() + "." + id.getPath();
    }

    public static Optional<Identifier> idOf(Holder<GemCut> entry) {
        return entry.unwrapKey().map(key -> key.identifier());
    }

    /// Display name of the cut gem, e.g. "Bold Ruby". Plain text: the cut's colour is for the socket glyph only.
    public static MutableComponent name(Holder<GemCut> entry) {
        var key = idOf(entry).map(GemCut::translationKey).orElse("gem_cut.unknown");
        return Component.translatable(key);
    }

    // MARK: Stacks

    /// A single cut gem of this cut.
    public static ItemStack stack(Holder<GemCut> entry) {
        var stack = new ItemStack(entry.value().gem());
        stack.set(GemComponents.CUT, entry);
        return stack;
    }

    public static Optional<Holder<GemCut>> of(ItemStack stack) {
        return Optional.ofNullable(stack.get(GemComponents.CUT));
    }
}
