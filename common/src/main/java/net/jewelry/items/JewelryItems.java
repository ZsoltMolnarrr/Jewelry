package net.jewelry.items;

import net.jewelry.JewelryMod;
import net.jewelry.config.ItemConfig;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchools;

import java.util.*;

public class JewelryItems {

    public static final ArrayList<Entry> all = new ArrayList<>();
    public static final class Entry {
        private final Identifier id;
        private final Rarity rarity;
        private final ItemConfig.Item config;
        private final String lore;
        private boolean fireproof;
        int tier = 0;

        public Item item;

        public Entry(Identifier id, Rarity rarity, ItemConfig.Item config, String lore, boolean fireproof) {
            this.id = id;
            this.rarity = rarity;
            this.config = config;
            this.lore = lore;
            this.fireproof = fireproof;
        }

        public Identifier id() {
            return id;
        }


        public Rarity rarity() {
            return rarity;
        }

        public ItemConfig.Item config() {
            return config;
        }

        public String lore() {
            return lore;
        }

        public boolean fireproof() {
            return fireproof;
        }

        public Item create(Item.Settings settings, JewelryModifiers attributes) {
            var slot = (id.getPath().contains("ring") ? "ring" : (id.getPath().contains("necklace") ? "necklace" : null));
            item = JewelryFactory.getFactory().apply(new JewelryFactory.ItemArgs(settings, attributes, lore, slot));
            return item;
        }

        public Item item() {
            return item;
        }

        public Entry setTier(int tier) {
            this.tier = tier;
            this.fireproof = tier >= 3;
            return this;
        }

        public int tier() {
            return tier;
        }
    }

    public static Entry add(Identifier id, ItemConfig.Item config) {
        return add(id, Rarity.COMMON, config, null, false);
    }

    public static Entry add(Identifier id, Rarity rarity, ItemConfig.Item config) {
        return add(id, rarity, config, null, false);
    }

    public static Entry add(Identifier id, Rarity rarity, ItemConfig.Item config, boolean fireproof) {
        return add(id, rarity, config, null, fireproof);
    }

    public static Entry add(Identifier id, Rarity rarity, boolean addLore, ItemConfig.Item config) {
        return add(id, rarity, config, addLore ? ("item." + id.getNamespace() + "." + id.getPath() + ".lore") : null, false);
    }

    public static Entry add(Identifier id, Rarity rarity, ItemConfig.Item config, String lore, boolean fireproof) {
        var entry = new Entry(id, rarity, config, lore, fireproof);
        all.add(entry);
        return entry;
    }

    private static final float tier_1_multiplier = 0.04F;
    private static final ItemConfig.Bonus tier_1_bonus = new ItemConfig.Bonus(tier_1_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE);
    private static final float tier_2_multiplier = 0.08F;
    private static final ItemConfig.Bonus tier_2_bonus = new ItemConfig.Bonus(tier_2_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE);

    // Attribute ids

    public static final String GENERIC_ARMOR = "generic.armor";
    public static final String GENERIC_LUCK = "generic.luck";
    public static final String GENERIC_MOVEMENT_SPEED = "generic.movement_speed";
    public static final String GENERIC_ATTACK_DAMAGE = "generic.attack_damage";
    public static final String GENERIC_MAX_HEALTH = "generic.max_health";
    public static final String GENERIC_ATTACK_SPEED = "generic.attack_speed";
    public static final String GENERIC_ARMOR_TOUGHNESS = "generic.armor_toughness";
    public static final String GENERIC_KNOCKBACK_RESISTANCE = "generic.knockback_resistance";

    public static final String COMBAT_ROLL_MOD_ID = "combat_roll";
    public static final String COMBATROLL_RECHARGE = COMBAT_ROLL_MOD_ID + ":recharge";
    public static final String COMBATROLL_COUNT = COMBAT_ROLL_MOD_ID + ":count";

    public static final String CRIT_MOD_ID = "critical_strike";
    public static final String CRITICAL_CHANCE_ID = CRIT_MOD_ID + ":chance";
    public static final String CRITICAL_DAMAGE_ID = CRIT_MOD_ID + ":damage";

    // RangedWeaponAPI attributes. A two-platform 1.20.1 RWA artifact does exist now, but Jewelry still
    // does NOT compile against it: these ids are only ever config strings, so they are spelled out and
    // resolved from the attribute registry at registration time (`register` below skips, and logs,
    // whatever is missing). No compile dependency, no `isModLoaded` holder class.
    public static final String RANGED_WEAPON_MOD_ID = "ranged_weapon";
    public static final String RANGED_WEAPON_DAMAGE = RANGED_WEAPON_MOD_ID + ":damage";
    public static final String RANGED_WEAPON_VELOCITY = RANGED_WEAPON_MOD_ID + ":velocity";
    public static final String RANGED_WEAPON_HASTE = RANGED_WEAPON_MOD_ID + ":haste";

    // MARK: Rings

    public static Entry copper_ring = add(new Identifier(JewelryMod.ID, "copper_ring"), ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ARMOR, 0.5F, EntityAttributeModifier.Operation.ADDITION)
            )
    )).setTier(0);

    public static Entry iron_ring = add(new Identifier(JewelryMod.ID, "iron_ring"), ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ARMOR, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    )).setTier(0);

    public static Entry gold_ring = add(new Identifier(JewelryMod.ID, "gold_ring"), Rarity.COMMON, true, ItemConfig.item(
            List.of(
            )
    )).setTier(0);


    public static Entry emerald_necklace = add(new Identifier(JewelryMod.ID, "emerald_necklace"), ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_LUCK, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    )).setTier(1);


    public static Entry diamond_necklace = add(new Identifier(JewelryMod.ID, "diamond_necklace"), ItemConfig.itemWithCondition(
            CRIT_MOD_ID,
            List.of(
                    new ItemConfig.AttributeModifier(CRITICAL_DAMAGE_ID , 0.08F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            ),
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_SPEED, tier_1_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(1);

    public static Entry diamond_ring = add(new Identifier(JewelryMod.ID, "diamond_ring"), ItemConfig.itemWithCondition(
            CRIT_MOD_ID,
            List.of(
                    new ItemConfig.AttributeModifier(CRITICAL_CHANCE_ID , 0.04F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            ),
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_SPEED, tier_1_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(1);

    // MARK: Custom gem rings

    // bold
    public static Entry ruby_ring = add(new Identifier(JewelryMod.ID, "ruby_ring"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_DAMAGE, tier_1_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(2);

    // sunfire
    public static Entry topaz_ring = add(new Identifier(JewelryMod.ID, "topaz_ring"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.ARCANE.id, tier_1_bonus),
                    new ItemConfig.AttributeModifier(SpellSchools.FIRE.id, tier_1_bonus)
            )
    )).setTier(2);

    public static Entry citrine_ring = add(new Identifier(JewelryMod.ID, "citrine_ring"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.HEALING.id, tier_1_bonus),
                    new ItemConfig.AttributeModifier(SpellSchools.LIGHTNING.id, tier_1_bonus)
            )
    )).setTier(2);

    // delicate
    public static Entry jade_ring = add(new Identifier(JewelryMod.ID, "jade_ring"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(RANGED_WEAPON_DAMAGE, tier_1_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(2);

    public static Entry sapphire_ring = add(new Identifier(JewelryMod.ID, "sapphire_ring"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_MAX_HEALTH, 2, EntityAttributeModifier.Operation.ADDITION)
            )
    )).setTier(2);

    public static Entry tanzanite_ring = add(new Identifier(JewelryMod.ID, "tanzanite_ring"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.FROST.id, tier_1_bonus),
                    new ItemConfig.AttributeModifier(SpellSchools.SOUL.id, tier_1_bonus)
            )
    )).setTier(2);

    // MARK: Custom gem necklaces

    public static Entry ruby_necklace = add(new Identifier(JewelryMod.ID, "ruby_necklace"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_DAMAGE, tier_1_bonus)
            )
    )).setTier(2);

    // sunfire
    public static Entry topaz_necklace = add(new Identifier(JewelryMod.ID, "topaz_necklace"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.ARCANE.id, tier_1_bonus),
                    new ItemConfig.AttributeModifier(SpellSchools.FIRE.id, tier_1_bonus)
            )
    )).setTier(2);

    public static Entry citrine_necklace = add(new Identifier(JewelryMod.ID, "citrine_necklace"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.HEALING.id, tier_1_bonus),
                    new ItemConfig.AttributeModifier(SpellSchools.LIGHTNING.id, tier_1_bonus)
            )
    )).setTier(2);

    // delicate
    public static Entry jade_necklace = add(new Identifier(JewelryMod.ID, "jade_necklace"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(RANGED_WEAPON_DAMAGE, tier_1_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(2);

    public static Entry sapphire_necklace = add(new Identifier(JewelryMod.ID, "sapphire_necklace"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_MAX_HEALTH, 2, EntityAttributeModifier.Operation.ADDITION)
            )
    )).setTier(2);

    public static Entry tanzanite_necklace = add(new Identifier(JewelryMod.ID, "tanzanite_necklace"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.FROST.id, tier_1_bonus),
                    new ItemConfig.AttributeModifier(SpellSchools.SOUL.id, tier_1_bonus)
            )
    )).setTier(2);


    // MARK: Netherite variants

    public static Entry netherite_ruby_ring = add(new Identifier(JewelryMod.ID, "netherite_ruby_ring"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_DAMAGE, tier_2_bonus)
            )
    )).setTier(3);

    public static Entry netherite_topaz_ring = add(new Identifier(JewelryMod.ID, "netherite_topaz_ring"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.ARCANE.id, tier_2_bonus),
                    new ItemConfig.AttributeModifier(SpellSchools.FIRE.id, tier_2_bonus)
            )
    )).setTier(3);

    public static Entry netherite_citrine_ring = add(new Identifier(JewelryMod.ID, "netherite_citrine_ring"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.HEALING.id, tier_2_bonus),
                    new ItemConfig.AttributeModifier(SpellSchools.LIGHTNING.id, tier_2_bonus)
            )
    )).setTier(3);

    public static Entry netherite_jade_ring = add(new Identifier(JewelryMod.ID, "netherite_jade_ring"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(RANGED_WEAPON_DAMAGE, tier_2_bonus)
            )
    )).setTier(3);

    public static Entry netherite_sapphire_ring = add(new Identifier(JewelryMod.ID, "netherite_sapphire_ring"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_MAX_HEALTH, 4, EntityAttributeModifier.Operation.ADDITION)
            )
    )).setTier(3);

    public static Entry netherite_tanzanite_ring = add(new Identifier(JewelryMod.ID, "netherite_tanzanite_ring"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.FROST.id, tier_2_bonus),
                    new ItemConfig.AttributeModifier(SpellSchools.SOUL.id, tier_2_bonus)
            )
    )).setTier(3);

    public static Entry netherite_ruby_necklace = add(new Identifier(JewelryMod.ID, "netherite_ruby_necklace"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_DAMAGE, tier_2_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(3);

    public static Entry netherite_topaz_necklace = add(new Identifier(JewelryMod.ID, "netherite_topaz_necklace"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.ARCANE.id, tier_2_bonus),
                    new ItemConfig.AttributeModifier(SpellSchools.FIRE.id, tier_2_bonus)
            )
    )).setTier(3);

    public static Entry netherite_citrine_necklace = add(new Identifier(JewelryMod.ID, "netherite_citrine_necklace"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.HEALING.id, tier_2_bonus),
                    new ItemConfig.AttributeModifier(SpellSchools.LIGHTNING.id, tier_2_bonus)
            )
    )).setTier(3);

    public static Entry nsetherite_jade_necklace = add(new Identifier(JewelryMod.ID, "netherite_jade_necklace"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(RANGED_WEAPON_DAMAGE, tier_2_bonus)
            )
    )).setTier(3);

    public static Entry netherite_sapphire_necklace = add(new Identifier(JewelryMod.ID, "netherite_sapphire_necklace"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_MAX_HEALTH, 4, EntityAttributeModifier.Operation.ADDITION)
            )
    )).setTier(3);

    public static Entry netherite_tanzanite_necklace = add(new Identifier(JewelryMod.ID, "netherite_tanzanite_necklace"), Rarity.UNCOMMON, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.FROST.id, tier_2_bonus),
                    new ItemConfig.AttributeModifier(SpellSchools.SOUL.id, tier_2_bonus)
            )
    )).setTier(3);

    // MARK: Unique pieces
    private static final float tier_3_physical_multiplier = 0.12F;
    private static final ItemConfig.Bonus tier_3_primary_bonus = new ItemConfig.Bonus(tier_3_physical_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE);

    private static final float tier_3_spell_multiplier = 0.08F;
    private static final float tier_3_ranged_multiplier = 0.08F;
    private static final float tier_3_secondary_multiplier = 0.03F;
    private static final ItemConfig.Bonus tier_3_spell_bonus = new ItemConfig.Bonus(tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE);


    public static Entry unique_attack_ring = add(new Identifier(JewelryMod.ID, "unique_attack_ring"), Rarity.RARE, true, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_DAMAGE, tier_3_primary_bonus),
                    new ItemConfig.AttributeModifier(GENERIC_KNOCKBACK_RESISTANCE, 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);

    public static Entry unique_attack_necklace = add(new Identifier(JewelryMod.ID, "unique_attack_necklace"), Rarity.RARE, true, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_DAMAGE, tier_3_primary_bonus),
                    new ItemConfig.AttributeModifier(GENERIC_KNOCKBACK_RESISTANCE, 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);

    public static Entry unique_dex_ring = add(new Identifier(JewelryMod.ID, "unique_dex_ring"), Rarity.RARE, true, ItemConfig.itemWithCondition(
            COMBAT_ROLL_MOD_ID,
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_SPEED, 0.06F, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_DAMAGE, 0.06F, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(COMBATROLL_RECHARGE, 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            ),
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_SPEED, 0.06F, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_DAMAGE, 0.06F, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(GENERIC_MOVEMENT_SPEED, 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);


    public static Entry unique_dex_necklace = add(new Identifier(JewelryMod.ID, "unique_dex_necklace"), Rarity.RARE, true, ItemConfig.itemWithCondition(
            COMBAT_ROLL_MOD_ID,
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_SPEED, 0.06F, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_DAMAGE, 0.06F, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(COMBATROLL_COUNT, 1F, EntityAttributeModifier.Operation.ADDITION)
            ),
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_SPEED, 0.06F, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_DAMAGE, 0.06F, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(GENERIC_MOVEMENT_SPEED, 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);

    public static Entry unique_tank_ring = add(new Identifier(JewelryMod.ID, "unique_tank_ring"), Rarity.RARE, true, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_MAX_HEALTH, 6F, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(GENERIC_ARMOR_TOUGHNESS, 1F, EntityAttributeModifier.Operation.ADDITION)
            )
    )).setTier(4);

    public static Entry unique_tank_necklace = add(new Identifier(JewelryMod.ID, "unique_tank_necklace"), Rarity.RARE, true, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_MAX_HEALTH, 6F, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(GENERIC_ARMOR_TOUGHNESS, 1F, EntityAttributeModifier.Operation.ADDITION)
            )
    )).setTier(4);

    public static Entry unique_archer_ring = add(new Identifier(JewelryMod.ID, "unique_archer_ring"), Rarity.RARE, true, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(RANGED_WEAPON_DAMAGE, tier_3_ranged_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(RANGED_WEAPON_VELOCITY, 0.5F, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(RANGED_WEAPON_HASTE , 0.04F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);

    public static Entry unique_archer_necklace = add(new Identifier(JewelryMod.ID, "unique_archer_necklace"), Rarity.RARE, true, ItemConfig.itemWithCondition(
            COMBAT_ROLL_MOD_ID,
            List.of(
                    new ItemConfig.AttributeModifier(RANGED_WEAPON_DAMAGE, tier_3_ranged_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(COMBATROLL_COUNT, 1F, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(RANGED_WEAPON_HASTE , 0.04F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            ),
            List.of(
                    new ItemConfig.AttributeModifier(RANGED_WEAPON_DAMAGE, tier_3_ranged_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(GENERIC_MOVEMENT_SPEED, 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(RANGED_WEAPON_HASTE , 0.04F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);

    public static Entry unique_arcane_ring = add(new Identifier(JewelryMod.ID, "unique_arcane_ring"), Rarity.RARE, true, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.ARCANE.id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.CRITICAL_CHANCE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.HASTE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);
    public static Entry unique_arcane_necklace = add(new Identifier(JewelryMod.ID, "unique_arcane_necklace"), Rarity.RARE, true, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.ARCANE.id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.CRITICAL_CHANCE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.HASTE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);

    public static Entry unique_fire_ring = add(new Identifier(JewelryMod.ID, "unique_fire_ring"), Rarity.RARE, true, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.FIRE.id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.CRITICAL_CHANCE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.CRITICAL_DAMAGE.id , 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);

    public static Entry unique_fire_necklace = add(new Identifier(JewelryMod.ID, "unique_fire_necklace"), Rarity.RARE, true, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.FIRE.id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.CRITICAL_CHANCE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.CRITICAL_DAMAGE.id , 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);

    public static Entry unique_frost_ring = add(new Identifier(JewelryMod.ID, "unique_frost_ring"), Rarity.RARE, true, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.FROST.id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.HASTE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.CRITICAL_DAMAGE.id , 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);

    public static Entry unique_frost_necklace = add(new Identifier(JewelryMod.ID, "unique_frost_necklace"), Rarity.RARE, true, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.FROST.id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.HASTE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.CRITICAL_DAMAGE.id , 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);

    public static Entry unique_healing_ring = add(new Identifier(JewelryMod.ID, "unique_healing_ring"), Rarity.RARE, true, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.HEALING.id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.HASTE.id , tier_3_secondary_multiplier * 2, EntityAttributeModifier.Operation.MULTIPLY_BASE)
                )
    )).setTier(4);

    public static Entry unique_healing_necklace = add(new Identifier(JewelryMod.ID, "unique_healing_necklace"), Rarity.RARE, true, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.HEALING.id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.HASTE.id , tier_3_secondary_multiplier * 2, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);

//    // DON'T FORGET LOOT TAGS! T4
//
//    public static Entry unique_lightning_ring = add(new Identifier(JewelryMod.ID, "unique_lightning_ring"), Rarity.RARE, true, ItemConfig.item(
//            List.of(
//                    new ItemConfig.AttributeModifier(SpellSchools.LIGHTNING.id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
//                    new ItemConfig.AttributeModifier(SpellPowerMechanics.CRITICAL_CHANCE.id, tier_3_secondary_multiplier * 2, EntityAttributeModifier.Operation.MULTIPLY_BASE)
//            )
//    )).setTier(4);
//
//    public static Entry unique_lightning_necklace = add(new Identifier(JewelryMod.ID, "unique_lightning_necklace"), Rarity.RARE, true, ItemConfig.item(
//            List.of(
//                    new ItemConfig.AttributeModifier(SpellSchools.LIGHTNING.id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
//                    new ItemConfig.AttributeModifier(SpellPowerMechanics.CRITICAL_CHANCE.id, tier_3_secondary_multiplier * 2, EntityAttributeModifier.Operation.MULTIPLY_BASE)
//            )
//    )).setTier(4);
//
//    public static Entry unique_soul_ring = add(new Identifier(JewelryMod.ID, "unique_soul_ring"), Rarity.RARE, true, ItemConfig.item(
//            List.of(
//                    new ItemConfig.AttributeModifier(SpellSchools.SOUL.id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
//                    new ItemConfig.AttributeModifier(SpellPowerMechanics.HASTE.id, tier_3_secondary_multiplier * 2, EntityAttributeModifier.Operation.MULTIPLY_BASE)
//            )
//    )).setTier(4);
//
//    public static Entry unique_soul_necklace = add(new Identifier(JewelryMod.ID, "unique_soul_necklace"), Rarity.RARE, true, ItemConfig.item(
//            List.of(
//                    new ItemConfig.AttributeModifier(SpellSchools.SOUL.id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
//                    new ItemConfig.AttributeModifier(SpellPowerMechanics.HASTE.id, tier_3_secondary_multiplier * 2, EntityAttributeModifier.Operation.MULTIPLY_BASE)
//            )
//    )).setTier(4);

    public static Entry unique_spell_ring = add(new Identifier(JewelryMod.ID, "unique_spell_ring"), Rarity.RARE, true, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.GENERIC.id, tier_3_spell_multiplier * 0.75F, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.CRITICAL_CHANCE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.HASTE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);
    public static Entry unique_spell_necklace = add(new Identifier(JewelryMod.ID, "unique_spell_necklace"), Rarity.RARE, true, ItemConfig.item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellSchools.GENERIC.id, tier_3_spell_multiplier * 1.25F, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellPowerMechanics.CRITICAL_DAMAGE.id , 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
//                    new ItemConfig.AttributeModifier(SpellPowerMechanics.CRITICAL_CHANCE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
//                    new ItemConfig.AttributeModifier(SpellPowerMechanics.HASTE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);

    public static final float tier_4_crit_chance = 0.04F;
    public static final float tier_4_crit_damage = 0.08F;
    public static Entry unique_crit_ring = add(new Identifier(JewelryMod.ID, "unique_crit_ring"), Rarity.RARE, true, ItemConfig.itemWithCondition(
            CRIT_MOD_ID,
            List.of(
                    new ItemConfig.AttributeModifier(CRITICAL_CHANCE_ID , tier_4_crit_chance * 2F, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(CRITICAL_DAMAGE_ID , tier_4_crit_damage, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            ),
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_DAMAGE , 0.15F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);

    public static Entry unique_crit_necklace = add(new Identifier(JewelryMod.ID, "unique_crit_necklace"), Rarity.RARE, true, ItemConfig.itemWithCondition(
            CRIT_MOD_ID,
            List.of(
                    new ItemConfig.AttributeModifier(CRITICAL_CHANCE_ID , tier_4_crit_chance, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(CRITICAL_DAMAGE_ID , tier_4_crit_damage * 2F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            ),
            List.of(
                    new ItemConfig.AttributeModifier(GENERIC_ATTACK_DAMAGE , 0.15F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    )).setTier(4);

    public static void register(ItemConfig allConfigs) {
        for (var entry : all) {
            ItemConfig.Item itemConfig = allConfigs.items.get(entry.id.toString());
            if (itemConfig == null) {
                itemConfig = entry.config;
                allConfigs.items.put(entry.id.toString(), entry.config);
            }

            var modifiers = new ArrayList<JewelryModifiers.Entry>();
            for (var modifier : itemConfig.selectedAttributes()) {
                var id = new Identifier(modifier.id);
                // 1.20.1 registries have no `getEntry(Identifier)`; an absent attribute (a companion
                // mod that is not installed, e.g. RangedWeaponAPI) simply drops its bonus.
                var attribute = Registries.ATTRIBUTE.getOrEmpty(id);
                if (attribute.isPresent()) {
                    // A per-item, per-attribute modifier id. A single shared id would make
                    // equipped jewelry pieces overwrite (rather than stack) each other's bonuses,
                    // since an attribute keys its modifiers by identifier (1.20.1: by the UUID
                    // derived from it -- see JewelryModifiers).
                    var modifierId = new Identifier(JewelryMod.ID,
                            entry.id().getPath() + "_" + id.getPath().replace('.', '_') + "_bonus");
                    modifiers.add(new JewelryModifiers.Entry(
                            attribute.get(), modifierId, modifier.value, modifier.operation));
                } else {
                    System.err.println("Failed to resolve EntityAttribute with id: " + modifier.id);
                }
            }
            var settings = new Item.Settings()
                    .rarity(entry.rarity)
                    .maxCount(1);
            if (entry.fireproof()) {
                settings = settings.fireproof();
            }

            var item = entry.create(settings.maxCount(1), new JewelryModifiers(List.copyOf(modifiers)));

            Registry.register(Registries.ITEM, entry.id(), item);
        }

        // Creative-tab placement is registered per-platform from each loader's entrypoint (iterating JewelryItems.all).
    }
}
