package net.jewelry.gems;

import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.jewelry.JewelryMod;
import net.jewelry.api.GemCutBuilder;
import net.jewelry.items.Gems;
import net.jewelry.items.JewelryItems;
import net.minecraft.util.Identifier;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;

/// The cuts Jewelry ships, built with the public [GemCutBuilder]. Source of truth for datagen, which emits
/// them as `data/jewelry/gem_cut/*.json`; other mods add theirs the same way (see docs/GEM_CUTS_API.md).
///
/// Four cuts per gem (five for sapphire), each gem a stat family (see docs/jewelry/GEM_CUTS_DESIGN.md):
/// Ruby = melee might · Sapphire = fortitude · Jade = agility/ranged · Topaz = arcane/fire & spell offense ·
/// Citrine = healing/lightning & tempo · Tanzanite = frost/soul & lethality.
/// Prefixes are unique across the set, so the name alone says the stat ("Quick" is always Attack Speed).
public class GemCuts {
    public static final List<GemCutBuilder.Entry> all = new ArrayList<>();

    // Balancing anchor: a cut gem costs the same raw gem as a tier-2 gem ring, so its primary bonus equals that
    // ring's (JewelryItems.tier_1_multiplier, +2 health); secondary stats sit at the uniques' secondary rate
    // or at half of what a tier-4 unique grants.
    private static final float PRIMARY_BONUS = JewelryItems.tier_1_multiplier;           // 4 %: damage, school power, speeds
    private static final float SECONDARY_BONUS = JewelryItems.tier_3_secondary_multiplier; // 3 %: generic spell power, spell crit chance, spell haste
    private static final float HALF_UNIQUE_BONUS = 0.05F;                                  // 5 %: uniques' 10 % secondaries halved
    public static final String CRITICAL_STRIKE = "critical_strike";
    public static final String COMBAT_ROLL = "combat_roll";
    public static final String SPELL_ENGINE = "spell_engine"; // not a dependency of Jewelry: its cuts are gated

    // One colour per gem, shared by all of its cuts
    private static final int RUBY = 0xE5404F;
    private static final int SAPPHIRE = 0x3F79E8;
    private static final int JADE = 0x4EC272;
    private static final int TOPAZ = 0xF2A33A;
    private static final int CITRINE = 0xF4D44B;
    private static final int TANZANITE = 0x8A5BE0;

    private static GemCutBuilder cut(String name, Gems.Entry gem, int color) {
        return GemCutBuilder.create(Identifier.of(JewelryMod.ID, name), gem.item()).color(color);
    }

    private static GemCutBuilder.Entry add(GemCutBuilder builder) {
        var entry = builder.build();
        all.add(entry);
        return entry;
    }

    private static Identifier vanilla(String path) {
        return Identifier.ofVanilla(path);
    }

    // MARK: Ruby — melee might
    public static final GemCutBuilder.Entry BOLD_RUBY = add(cut("bold_ruby", Gems.ruby, RUBY)
            .multiplyBase(vanilla("generic.attack_damage"), PRIMARY_BONUS));
    public static final GemCutBuilder.Entry QUICK_RUBY = add(cut("quick_ruby", Gems.ruby, RUBY)
            .multiplyBase(vanilla("generic.attack_speed"), PRIMARY_BONUS));
    public static final GemCutBuilder.Entry FIERCE_RUBY = add(cut("fierce_ruby", Gems.ruby, RUBY)
            .multiplyBase(Identifier.of(CRITICAL_STRIKE, "damage"), 0.08F)
            .requiresMod(CRITICAL_STRIKE));
    public static final GemCutBuilder.Entry KEEN_RUBY = add(cut("keen_ruby", Gems.ruby, RUBY)
            .multiplyBase(Identifier.of(CRITICAL_STRIKE, "chance"), 0.04F)
            .requiresMod(CRITICAL_STRIKE));

    // MARK: Sapphire — fortitude
    public static final GemCutBuilder.Entry SOLID_SAPPHIRE = add(cut("solid_sapphire", Gems.sapphire, SAPPHIRE)
            .addValue(vanilla("generic.max_health"), 2F));
    public static final GemCutBuilder.Entry STURDY_SAPPHIRE = add(cut("sturdy_sapphire", Gems.sapphire, SAPPHIRE)
            .addValue(vanilla("generic.armor"), 1F));
    public static final GemCutBuilder.Entry RIGID_SAPPHIRE = add(cut("rigid_sapphire", Gems.sapphire, SAPPHIRE)
            .addValue(vanilla("generic.armor_toughness"), 0.5F));
    public static final GemCutBuilder.Entry STEADFAST_SAPPHIRE = add(cut("steadfast_sapphire", Gems.sapphire, SAPPHIRE)
            .addValue(vanilla("generic.knockback_resistance"), 0.1F));
    public static final GemCutBuilder.Entry VITAL_SAPPHIRE = add(cut("vital_sapphire", Gems.sapphire, SAPPHIRE)
            .multiplyBase(Identifier.of(SPELL_ENGINE, "healing_taken"), 0.02F)
            .requiresMod(SPELL_ENGINE));

    // MARK: Jade — agility, ranged
    public static final GemCutBuilder.Entry PRECISE_JADE = add(cut("precise_jade", Gems.jade, JADE)
            .multiplyBase(EntityAttributes_RangedWeapon.DAMAGE.id, PRIMARY_BONUS));
    public static final GemCutBuilder.Entry SWIFT_JADE = add(cut("swift_jade", Gems.jade, JADE)
            .multiplyBase(EntityAttributes_RangedWeapon.HASTE.id, PRIMARY_BONUS));
    public static final GemCutBuilder.Entry PIERCING_JADE = add(cut("piercing_jade", Gems.jade, JADE)
            .addValue(EntityAttributes_RangedWeapon.VELOCITY.id, 0.25F));
    public static final GemCutBuilder.Entry NIMBLE_JADE = add(cut("nimble_jade", Gems.jade, JADE)
            .multiplyBase(Identifier.of(COMBAT_ROLL, "recharge"), PRIMARY_BONUS)
            .requiresMod(COMBAT_ROLL));

    // MARK: Topaz — arcane & fire, spell offense
    public static final GemCutBuilder.Entry RUNED_TOPAZ = add(cut("runed_topaz", Gems.topaz, TOPAZ)
            .multiplyBase(SpellSchools.ARCANE.id, PRIMARY_BONUS));
    public static final GemCutBuilder.Entry FLAMING_TOPAZ = add(cut("flaming_topaz", Gems.topaz, TOPAZ)
            .multiplyBase(SpellSchools.FIRE.id, PRIMARY_BONUS));
    public static final GemCutBuilder.Entry BRILLIANT_TOPAZ = add(cut("brilliant_topaz", Gems.topaz, TOPAZ)
            .multiplyBase(SpellSchools.GENERIC.id, SECONDARY_BONUS));
    public static final GemCutBuilder.Entry GLEAMING_TOPAZ = add(cut("gleaming_topaz", Gems.topaz, TOPAZ)
            .multiplyBase(SpellPowerMechanics.CRITICAL_CHANCE.id, SECONDARY_BONUS));

    // MARK: Citrine — healing & lightning, tempo
    public static final GemCutBuilder.Entry RADIANT_CITRINE = add(cut("radiant_citrine", Gems.citrine, CITRINE)
            .multiplyBase(SpellSchools.HEALING.id, PRIMARY_BONUS));
    public static final GemCutBuilder.Entry SPARKING_CITRINE = add(cut("sparking_citrine", Gems.citrine, CITRINE)
            .multiplyBase(SpellSchools.LIGHTNING.id, PRIMARY_BONUS));
    public static final GemCutBuilder.Entry RECKLESS_CITRINE = add(cut("reckless_citrine", Gems.citrine, CITRINE)
            .multiplyBase(SpellPowerMechanics.HASTE.id, SECONDARY_BONUS));
    public static final GemCutBuilder.Entry FLEET_CITRINE = add(cut("fleet_citrine", Gems.citrine, CITRINE)
            .multiplyBase(vanilla("generic.movement_speed"), HALF_UNIQUE_BONUS));

    // MARK: Tanzanite — frost & soul, lethality & elusiveness
    public static final GemCutBuilder.Entry GLACIAL_TANZANITE = add(cut("glacial_tanzanite", Gems.tanzanite, TANZANITE)
            .multiplyBase(SpellSchools.FROST.id, PRIMARY_BONUS));
    public static final GemCutBuilder.Entry SHADOWY_TANZANITE = add(cut("shadowy_tanzanite", Gems.tanzanite, TANZANITE)
            .multiplyBase(SpellSchools.SOUL.id, PRIMARY_BONUS));
    public static final GemCutBuilder.Entry WICKED_TANZANITE = add(cut("wicked_tanzanite", Gems.tanzanite, TANZANITE)
            .multiplyBase(SpellPowerMechanics.CRITICAL_DAMAGE.id, HALF_UNIQUE_BONUS));
    public static final GemCutBuilder.Entry ELUSIVE_TANZANITE = add(cut("elusive_tanzanite", Gems.tanzanite, TANZANITE)
            .multiplyBase(Identifier.of(SPELL_ENGINE, "evasion_chance"), PRIMARY_BONUS)
            .requiresMod(SPELL_ENGINE));
}
