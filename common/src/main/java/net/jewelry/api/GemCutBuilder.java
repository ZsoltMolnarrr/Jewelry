package net.jewelry.api;

import net.jewelry.gems.GemCut;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/// Builds a gem cut definition ready for datagen (see `net.jewelry.api.datagen.GemCutGenerator` in the
/// Fabric module) — the public entry point for other mods adding cuts.
///
/// ```java
/// GemCutBuilder.create(Identifier.of("mymod", "bold_garnet"), MyItems.GARNET)
///         .multiplyBase(Identifier.ofVanilla("generic.attack_damage"), 0.05F)
///         .color(0xB0303A)
///         .build();
/// ```
///
/// Defaults: the model is `<ns>:item/gem_cut/<path>` (a file under `models/item/gem_cut/`, which Jewelry
/// discovers and bakes on both loaders; `GemCutGenerator.generateItemModel` emits it), the colour is white.
/// The raw gem item should extend `net.jewelry.gems.GemItem` so the cut gem takes the cut's name and
/// tooltip; the cut's name is the lang key `gem_cut.<ns>.<path>`, e.g. "Bold Garnet".
public class GemCutBuilder {
    /// A cut ready to generate: its registry id, the definition, and (optionally) a mod it needs.
    /// `requiredMod` becomes load conditions for both loaders in the generated JSON, so the cut only
    /// exists when that mod is installed.
    public record Entry(Identifier id, GemCut definition, @Nullable String requiredMod) {
        public Optional<Identifier> model() {
            return definition.model();
        }
    }

    private final Identifier id;
    private final RegistryEntry<Item> gem;
    private Identifier attribute;
    private float value;
    private EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE;
    private TextColor color = TextColor.fromRgb(0xFFFFFF);
    private Optional<Identifier> model;
    private String requiredMod;

    private GemCutBuilder(Identifier id, RegistryEntry<Item> gem) {
        this.id = id;
        this.gem = gem;
        this.model = Optional.of(GemCut.conventionalModelId(id));
    }

    public static GemCutBuilder create(Identifier id, ItemConvertible gem) {
        return new GemCutBuilder(id, gem.asItem().getRegistryEntry());
    }

    public static GemCutBuilder create(Identifier id, RegistryEntry<Item> gem) {
        return new GemCutBuilder(id, gem);
    }

    // MARK: Attribute (exactly one per cut)

    public GemCutBuilder attribute(Identifier attribute, float value, EntityAttributeModifier.Operation operation) {
        this.attribute = attribute;
        this.value = value;
        this.operation = operation;
        return this;
    }

    public GemCutBuilder attribute(RegistryEntry<EntityAttribute> attribute, float value, EntityAttributeModifier.Operation operation) {
        return attribute(attribute.getKey().orElseThrow().getValue(), value, operation);
    }

    /// `+value×100 %` of the attribute's base, the usual shape for damage and spell power bonuses.
    public GemCutBuilder multiplyBase(Identifier attribute, float value) {
        return attribute(attribute, value, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    public GemCutBuilder multiplyBase(RegistryEntry<EntityAttribute> attribute, float value) {
        return attribute(attribute, value, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    /// A flat `+value`, for health, armor and the like.
    public GemCutBuilder addValue(Identifier attribute, float value) {
        return attribute(attribute, value, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    public GemCutBuilder addValue(RegistryEntry<EntityAttribute> attribute, float value) {
        return attribute(attribute, value, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    // MARK: Presentation

    /// Tint of the socket glyph on equipment tooltips (the cut's name stays plain).
    public GemCutBuilder color(TextColor color) {
        this.color = color;
        return this;
    }

    public GemCutBuilder color(int rgb) {
        return color(TextColor.fromRgb(rgb));
    }

    /// Custom item model id instead of the `<ns>:item/gem_cut/<path>` default.
    public GemCutBuilder model(Identifier model) {
        this.model = Optional.of(model);
        return this;
    }

    /// Render the cut gem with the raw gem's own model.
    public GemCutBuilder noModel() {
        this.model = Optional.empty();
        return this;
    }

    // MARK: Loading

    /// Only load this cut when `modId` is installed (both loaders' load conditions are generated).
    public GemCutBuilder requiresMod(String modId) {
        this.requiredMod = modId;
        return this;
    }

    public Entry build() {
        if (attribute == null) {
            throw new IllegalStateException("Gem cut " + id + " has no attribute");
        }
        return new Entry(id, new GemCut(gem, attribute, value, operation, color, model), requiredMod);
    }
}
