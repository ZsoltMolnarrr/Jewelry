package net.jewelry.gems;

import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.jewelry.JewelryMod;
import net.jewelry.client.CustomModels;
import net.jewelry.items.Gems;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/// The cuts Jewelry ships. Source of truth for datagen, which emits them as `data/jewelry/gem_cut/*.json`;
/// data packs can add further cuts without touching this class.
///
/// First pass mirrors the gem rings: one cut per attribute a ring of that gem grants.
public class GemCuts {
    public record Entry(Identifier id, Gems.Entry gem, Identifier attribute, float value,
                        EntityAttributeModifier.Operation operation, TextColor color) {
        /// Model id by convention: `jewelry:item/gem_cut/<cut>` (datagen emits the model file).
        public Identifier model() {
            return CustomModels.gemCutModelId(id);
        }

        public GemCut definition() {
            return new GemCut(gem.item().getRegistryEntry(), attribute, value, operation, color, Optional.of(model()));
        }
    }

    public static final List<Entry> all = new ArrayList<>();

    private static final float PERCENT_BONUS = 0.05F;
    private static final EntityAttributeModifier.Operation MULTIPLY_BASE = EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE;
    private static final EntityAttributeModifier.Operation ADD = EntityAttributeModifier.Operation.ADD_VALUE;

    // One colour per gem, shared by all of its cuts
    private static final TextColor RUBY = TextColor.fromRgb(0xE5404F);
    private static final TextColor SAPPHIRE = TextColor.fromRgb(0x3F79E8);
    private static final TextColor JADE = TextColor.fromRgb(0x4EC272);
    private static final TextColor TOPAZ = TextColor.fromRgb(0xF2A33A);
    private static final TextColor CITRINE = TextColor.fromRgb(0xF4D44B);
    private static final TextColor TANZANITE = TextColor.fromRgb(0x8A5BE0);

    private static Entry add(String name, Gems.Entry gem, Identifier attribute, float value,
                             EntityAttributeModifier.Operation operation, TextColor color) {
        var entry = new Entry(Identifier.of(JewelryMod.ID, name), gem, attribute, value, operation, color);
        all.add(entry);
        return entry;
    }

    private static Identifier vanilla(String path) {
        return Identifier.ofVanilla(path);
    }

    public static final Entry BOLD_RUBY = add("bold_ruby", Gems.ruby,
            vanilla("generic.attack_damage"), PERCENT_BONUS, MULTIPLY_BASE, RUBY);
    public static final Entry SOLID_SAPPHIRE = add("solid_sapphire", Gems.sapphire,
            vanilla("generic.max_health"), 1F, ADD, SAPPHIRE);
    public static final Entry PRECISE_JADE = add("precise_jade", Gems.jade,
            EntityAttributes_RangedWeapon.DAMAGE.id, PERCENT_BONUS, MULTIPLY_BASE, JADE);
    public static final Entry RUNED_TOPAZ = add("runed_topaz", Gems.topaz,
            SpellSchools.ARCANE.id, PERCENT_BONUS, MULTIPLY_BASE, TOPAZ);
    public static final Entry FLAMING_TOPAZ = add("flaming_topaz", Gems.topaz,
            SpellSchools.FIRE.id, PERCENT_BONUS, MULTIPLY_BASE, TOPAZ);
    public static final Entry RADIANT_CITRINE = add("radiant_citrine", Gems.citrine,
            SpellSchools.HEALING.id, PERCENT_BONUS, MULTIPLY_BASE, CITRINE);
    public static final Entry SPARKING_CITRINE = add("sparking_citrine", Gems.citrine,
            SpellSchools.LIGHTNING.id, PERCENT_BONUS, MULTIPLY_BASE, CITRINE);
    public static final Entry GLACIAL_TANZANITE = add("glacial_tanzanite", Gems.tanzanite,
            SpellSchools.FROST.id, PERCENT_BONUS, MULTIPLY_BASE, TANZANITE);
    public static final Entry SHADOWY_TANZANITE = add("shadowy_tanzanite", Gems.tanzanite,
            SpellSchools.SOUL.id, PERCENT_BONUS, MULTIPLY_BASE, TANZANITE);
}
