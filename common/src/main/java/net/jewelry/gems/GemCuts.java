package net.jewelry.gems;

import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.jewelry.JewelryMod;
import net.jewelry.api.GemCutBuilder;
import net.jewelry.items.Gems;
import net.minecraft.util.Identifier;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;

/// The cuts Jewelry ships, built with the public [GemCutBuilder]. Source of truth for datagen, which emits
/// them as `data/jewelry/gem_cut/*.json`; other mods add theirs the same way (see docs/GEM_CUTS_API.md).
///
/// Four cuts per gem (three for tanzanite), each gem a stat family (see docs/jewelry/GEM_CUTS_DESIGN.md):
/// Ruby = melee might · Sapphire = fortitude · Jade = agility/ranged · Topaz = arcane/fire & spell offense ·
/// Citrine = healing/lightning & tempo · Tanzanite = frost/soul & lethality.
/// Prefixes are unique across the set, so the name alone says the stat ("Quick" is always Attack Speed).
public class GemCuts {
    public static final List<GemCutBuilder.Entry> all = new ArrayList<>();

    private static final float PERCENT_BONUS = 0.05F;       // damage / school power
    private static final float MECHANIC_BONUS = 0.03F;      // generic spell power, spell crit chance, haste, movement
    public static final String CRITICAL_STRIKE = "critical_strike";
    public static final String COMBAT_ROLL = "combat_roll";

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
            .multiplyBase(vanilla("generic.attack_damage"), PERCENT_BONUS));
    public static final GemCutBuilder.Entry QUICK_RUBY = add(cut("quick_ruby", Gems.ruby, RUBY)
            .multiplyBase(vanilla("generic.attack_speed"), PERCENT_BONUS));
    public static final GemCutBuilder.Entry FIERCE_RUBY = add(cut("fierce_ruby", Gems.ruby, RUBY)
            .multiplyBase(Identifier.of(CRITICAL_STRIKE, "damage"), 0.08F)
            .requiresMod(CRITICAL_STRIKE));
    public static final GemCutBuilder.Entry KEEN_RUBY = add(cut("keen_ruby", Gems.ruby, RUBY)
            .multiplyBase(Identifier.of(CRITICAL_STRIKE, "chance"), 0.04F)
            .requiresMod(CRITICAL_STRIKE));

    // MARK: Sapphire — fortitude
    public static final GemCutBuilder.Entry SOLID_SAPPHIRE = add(cut("solid_sapphire", Gems.sapphire, SAPPHIRE)
            .addValue(vanilla("generic.max_health"), 1F));
    public static final GemCutBuilder.Entry STURDY_SAPPHIRE = add(cut("sturdy_sapphire", Gems.sapphire, SAPPHIRE)
            .addValue(vanilla("generic.armor"), 1F));
    public static final GemCutBuilder.Entry RIGID_SAPPHIRE = add(cut("rigid_sapphire", Gems.sapphire, SAPPHIRE)
            .addValue(vanilla("generic.armor_toughness"), 0.5F));
    public static final GemCutBuilder.Entry STEADFAST_SAPPHIRE = add(cut("steadfast_sapphire", Gems.sapphire, SAPPHIRE)
            .addValue(vanilla("generic.knockback_resistance"), 0.1F));

    // MARK: Jade — agility, ranged
    public static final GemCutBuilder.Entry PRECISE_JADE = add(cut("precise_jade", Gems.jade, JADE)
            .multiplyBase(EntityAttributes_RangedWeapon.DAMAGE.id, PERCENT_BONUS));
    public static final GemCutBuilder.Entry SWIFT_JADE = add(cut("swift_jade", Gems.jade, JADE)
            .multiplyBase(EntityAttributes_RangedWeapon.HASTE.id, PERCENT_BONUS));
    public static final GemCutBuilder.Entry PIERCING_JADE = add(cut("piercing_jade", Gems.jade, JADE)
            .addValue(EntityAttributes_RangedWeapon.VELOCITY.id, 0.25F));
    public static final GemCutBuilder.Entry EVASIVE_JADE = add(cut("evasive_jade", Gems.jade, JADE)
            .multiplyBase(Identifier.of(COMBAT_ROLL, "recharge"), 0.10F)
            .requiresMod(COMBAT_ROLL));

    // MARK: Topaz — arcane & fire, spell offense
    public static final GemCutBuilder.Entry RUNED_TOPAZ = add(cut("runed_topaz", Gems.topaz, TOPAZ)
            .multiplyBase(SpellSchools.ARCANE.id, PERCENT_BONUS));
    public static final GemCutBuilder.Entry FLAMING_TOPAZ = add(cut("flaming_topaz", Gems.topaz, TOPAZ)
            .multiplyBase(SpellSchools.FIRE.id, PERCENT_BONUS));
    public static final GemCutBuilder.Entry BRILLIANT_TOPAZ = add(cut("brilliant_topaz", Gems.topaz, TOPAZ)
            .multiplyBase(SpellSchools.GENERIC.id, MECHANIC_BONUS));
    public static final GemCutBuilder.Entry GLEAMING_TOPAZ = add(cut("gleaming_topaz", Gems.topaz, TOPAZ)
            .multiplyBase(SpellPowerMechanics.CRITICAL_CHANCE.id, MECHANIC_BONUS));

    // MARK: Citrine — healing & lightning, tempo
    public static final GemCutBuilder.Entry RADIANT_CITRINE = add(cut("radiant_citrine", Gems.citrine, CITRINE)
            .multiplyBase(SpellSchools.HEALING.id, PERCENT_BONUS));
    public static final GemCutBuilder.Entry SPARKING_CITRINE = add(cut("sparking_citrine", Gems.citrine, CITRINE)
            .multiplyBase(SpellSchools.LIGHTNING.id, PERCENT_BONUS));
    public static final GemCutBuilder.Entry RECKLESS_CITRINE = add(cut("reckless_citrine", Gems.citrine, CITRINE)
            .multiplyBase(SpellPowerMechanics.HASTE.id, MECHANIC_BONUS));
    public static final GemCutBuilder.Entry FLEET_CITRINE = add(cut("fleet_citrine", Gems.citrine, CITRINE)
            .multiplyBase(vanilla("generic.movement_speed"), MECHANIC_BONUS));

    // MARK: Tanzanite — frost & soul, lethality
    public static final GemCutBuilder.Entry GLACIAL_TANZANITE = add(cut("glacial_tanzanite", Gems.tanzanite, TANZANITE)
            .multiplyBase(SpellSchools.FROST.id, PERCENT_BONUS));
    public static final GemCutBuilder.Entry SHADOWY_TANZANITE = add(cut("shadowy_tanzanite", Gems.tanzanite, TANZANITE)
            .multiplyBase(SpellSchools.SOUL.id, PERCENT_BONUS));
    public static final GemCutBuilder.Entry WICKED_TANZANITE = add(cut("wicked_tanzanite", Gems.tanzanite, TANZANITE)
            .multiplyBase(SpellPowerMechanics.CRITICAL_DAMAGE.id, PERCENT_BONUS));
}
