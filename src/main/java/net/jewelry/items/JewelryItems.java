package net.jewelry.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.jewelry.JewelryMod;
import net.jewelry.api.AttributeResolver;
import net.jewelry.api.JewelryItem;
import net.jewelry.config.ItemConfig;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.projectile_damage.api.EntityAttributes_ProjectileDamage;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.SpellAttributes;

import java.util.*;

public class JewelryItems {
    public record Entry(Identifier id, JewelryItem item, ItemConfig.Item config) {  }
    public static final ArrayList<Entry> all = new ArrayList<>();
    public static final Map<String, Item> entryMap = new HashMap<>();

    public static Entry add(Identifier id, ItemConfig.Item config) {
        return add(id, Rarity.COMMON, config, null);
    }

    public static Entry add(Identifier id, Rarity rarity, ItemConfig.Item config) {
        return add(id, rarity, config, null);
    }

    public static Entry add(Identifier id, Rarity rarity, boolean addLore, ItemConfig.Item config) {
        return add(id, rarity, config, addLore ? ("item." + id.getNamespace() + "." + id.getPath() + ".lore") : null);
    }

    public static Entry add(Identifier id, Rarity rarity, ItemConfig.Item config, String lore) {
        var entry = new Entry(id, new JewelryItem(new FabricItemSettings().rarity(rarity).group(Group.JEWELRY), lore), config);
        all.add(entry);
        entryMap.put(id.toString(), entry.item());
        return entry;
    }

    private static final float tier_1_multiplier = 0.04F;
    private static final ItemConfig.Bonus tier_1_bonus = new ItemConfig.Bonus(tier_1_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE);
    private static final float tier_2_multiplier = 0.08F;
    private static final ItemConfig.Bonus tier_2_bonus = new ItemConfig.Bonus(tier_2_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE);


    // MARK: Rings

    public static Entry copper_ring = add(new Identifier(JewelryMod.ID, "copper_ring"), new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("generic.armor", 0.5F, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry iron_ring = add(new Identifier(JewelryMod.ID, "iron_ring"), new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("generic.armor", 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry gold_ring = add(new Identifier(JewelryMod.ID, "gold_ring"), new ItemConfig.Item(
            List.of(
            )
    ));

    public static Entry emerald_necklace = add(new Identifier(JewelryMod.ID, "emerald_necklace"), new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("generic.luck", 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry diamond_necklace = add(new Identifier(JewelryMod.ID, "diamond_necklace"), new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("generic.movement_speed", 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    // MARK: Custom gem rings

    // bold
    public static Entry ruby_ring = add(new Identifier(JewelryMod.ID, "ruby_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.attack_damage", tier_1_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    // sunfire
    public static Entry topaz_ring = add(new Identifier(JewelryMod.ID, "topaz_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, tier_1_bonus),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, tier_1_bonus)
            )
    ));

    public static Entry citrine_ring = add(new Identifier(JewelryMod.ID, "citrine_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, tier_1_bonus),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, tier_1_bonus)
            )
    ));

    // delicate
    public static Entry jade_ring = add(new Identifier(JewelryMod.ID, "jade_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(EntityAttributes_ProjectileDamage.attributeId, tier_1_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    public static Entry sapphire_ring = add(new Identifier(JewelryMod.ID, "sapphire_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.max_health", 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry tanzanite_ring = add(new Identifier(JewelryMod.ID, "tanzanite_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FROST).id, tier_1_bonus),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.SOUL).id, tier_1_bonus)
            )
    ));

    // MARK: Custom gem necklaces

    public static Entry ruby_necklace = add(new Identifier(JewelryMod.ID, "ruby_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("generic.attack_damage", tier_1_bonus)
            )
    ));

    // sunfire
    public static Entry topaz_necklace = add(new Identifier(JewelryMod.ID, "topaz_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, tier_1_bonus),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, tier_1_bonus)
            )
    ));

    public static Entry citrine_necklace = add(new Identifier(JewelryMod.ID, "citrine_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, tier_1_bonus),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, tier_1_bonus)
            )
    ));

    // delicate
    public static Entry jade_necklace = add(new Identifier(JewelryMod.ID, "jade_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(EntityAttributes_ProjectileDamage.attributeId, tier_1_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    public static Entry sapphire_necklace = add(new Identifier(JewelryMod.ID, "sapphire_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.max_health", 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry tanzanite_necklace = add(new Identifier(JewelryMod.ID, "tanzanite_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FROST).id, tier_1_bonus),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.SOUL).id, tier_1_bonus)
            )
    ));


    // MARK: Netherite variants

    public static Entry netherite_ruby_ring = add(new Identifier(JewelryMod.ID, "netherite_ruby_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.attack_damage", tier_2_bonus)
            )
    ));

    public static Entry netherite_topaz_ring = add(new Identifier(JewelryMod.ID, "netherite_topaz_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, tier_2_bonus),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, tier_2_bonus)
            )
    ));

    public static Entry netherite_citrine_ring = add(new Identifier(JewelryMod.ID, "netherite_citrine_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, tier_2_bonus),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, tier_2_bonus)
            )
    ));

    public static Entry netherite_jade_ring = add(new Identifier(JewelryMod.ID, "netherite_jade_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(EntityAttributes_ProjectileDamage.attributeId, tier_2_bonus)
            )
    ));

    public static Entry netherite_sapphire_ring = add(new Identifier(JewelryMod.ID, "netherite_sapphire_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.max_health", tier_2_bonus)
            )
    ));

    public static Entry netherite_tanzanite_ring = add(new Identifier(JewelryMod.ID, "netherite_tanzanite_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FROST).id, tier_2_bonus),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.SOUL).id, tier_2_bonus)
            )
    ));

    public static Entry netherite_ruby_necklace = add(new Identifier(JewelryMod.ID, "netherite_ruby_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("generic.attack_damage", tier_2_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    public static Entry netherite_topaz_necklace = add(new Identifier(JewelryMod.ID, "netherite_topaz_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, tier_2_bonus),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, tier_2_bonus)
            )
    ));

    public static Entry netherite_citrine_necklace = add(new Identifier(JewelryMod.ID, "netherite_citrine_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, tier_2_bonus),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, tier_2_bonus)
            )
    ));

    public static Entry netherite_jade_necklace = add(new Identifier(JewelryMod.ID, "netherite_jade_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(EntityAttributes_ProjectileDamage.attributeId, tier_2_bonus)
            )
    ));

    public static Entry netherite_sapphire_necklace = add(new Identifier(JewelryMod.ID, "netherite_sapphire_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.max_health", 4, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_tanzanite_necklace = add(new Identifier(JewelryMod.ID, "netherite_tanzanite_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FROST).id, tier_2_bonus),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.SOUL).id, tier_2_bonus)
            )
    ));

    // MARK: Unique pieces
    private static final float tier_3_physical_multiplier = 0.12F;
    private static final ItemConfig.Bonus tier_3_primary_bonus = new ItemConfig.Bonus(tier_3_physical_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE);

    private static final float tier_3_spell_multiplier = 0.08F;
    private static final float tier_3_secondary_multiplier = 0.03F;
    private static final ItemConfig.Bonus tier_3_spell_bonus = new ItemConfig.Bonus(tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE);


    public static Entry unique_attack_ring = add(new Identifier(JewelryMod.ID, "unique_attack_ring"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.attack_damage", tier_3_primary_bonus)
            )
    ));

    public static Entry unique_attack_necklace = add(new Identifier(JewelryMod.ID, "unique_attack_necklace"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.attack_damage", tier_3_primary_bonus)
            )
    ));

    public static Entry unique_dex_ring = add(new Identifier(JewelryMod.ID, "unique_dex_ring"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.attack_damage", 0.08F, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier("combatroll:recharge", 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    public static Entry unique_dex_necklace = add(new Identifier(JewelryMod.ID, "unique_dex_necklace"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.attack_damage", 0.08F, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier("combatroll:count", 1F, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry unique_tank_ring = add(new Identifier(JewelryMod.ID, "unique_tank_ring"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.max_health", 6F, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier("minecraft:generic.armor_toughness", 1F, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry unique_tank_necklace = add(new Identifier(JewelryMod.ID, "unique_tank_necklace"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.max_health", 6F, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier("minecraft:generic.armor_toughness", 1F, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry unique_archer_ring = add(new Identifier(JewelryMod.ID, "unique_archer_ring"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(EntityAttributes_ProjectileDamage.attributeId, tier_3_physical_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier("minecraft:generic.movement_speed", 0.05F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    public static Entry unique_archer_necklace = add(new Identifier(JewelryMod.ID, "unique_archer_necklace"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(EntityAttributes_ProjectileDamage.attributeId, tier_3_physical_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier("minecraft:generic.movement_speed", 0.05F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    public static Entry unique_arcane_ring = add(new Identifier(JewelryMod.ID, "unique_arcane_ring"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellAttributes.CRITICAL_CHANCE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellAttributes.HASTE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));
    public static Entry unique_arcane_necklace = add(new Identifier(JewelryMod.ID, "unique_arcane_necklace"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellAttributes.CRITICAL_CHANCE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellAttributes.HASTE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    public static Entry unique_fire_ring = add(new Identifier(JewelryMod.ID, "unique_fire_ring"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellAttributes.CRITICAL_CHANCE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellAttributes.CRITICAL_DAMAGE.id , 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    public static Entry unique_fire_necklace = add(new Identifier(JewelryMod.ID, "unique_fire_necklace"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellAttributes.CRITICAL_CHANCE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellAttributes.CRITICAL_DAMAGE.id , 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    public static Entry unique_frost_ring = add(new Identifier(JewelryMod.ID, "unique_frost_ring"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FROST).id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellAttributes.HASTE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellAttributes.CRITICAL_DAMAGE.id , 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    public static Entry unique_frost_necklace = add(new Identifier(JewelryMod.ID, "unique_frost_necklace"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FROST).id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellAttributes.HASTE.id , tier_3_secondary_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellAttributes.CRITICAL_DAMAGE.id , 0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    public static Entry unique_healing_ring = add(new Identifier(JewelryMod.ID, "unique_healing_ring"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellAttributes.HASTE.id , tier_3_secondary_multiplier * 2, EntityAttributeModifier.Operation.MULTIPLY_BASE)
                )
    ));

    public static Entry unique_healing_necklace = add(new Identifier(JewelryMod.ID, "unique_healing_necklace"), Rarity.RARE, true, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
                    new ItemConfig.AttributeModifier(SpellAttributes.HASTE.id , tier_3_secondary_multiplier * 2, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

//    public static Entry unique_lightning_ring = add(new Identifier(JewelryMod.ID, "unique_lightning_ring"), Rarity.RARE, true, new ItemConfig.Item(
//            List.of(
//                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
//                    new ItemConfig.AttributeModifier(SpellAttributes.CRITICAL_CHANCE.id, tier_3_secondary_multiplier * 2, EntityAttributeModifier.Operation.MULTIPLY_BASE)
//            )
//    ));
//
//    public static Entry unique_lightning_necklace = add(new Identifier(JewelryMod.ID, "unique_lightning_necklace"), Rarity.RARE, true, new ItemConfig.Item(
//            List.of(
//                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
//                    new ItemConfig.AttributeModifier(SpellAttributes.CRITICAL_CHANCE.id, tier_3_secondary_multiplier * 2, EntityAttributeModifier.Operation.MULTIPLY_BASE)
//            )
//    ));
//
//    public static Entry unique_soul_ring = add(new Identifier(JewelryMod.ID, "unique_soul_ring"), Rarity.RARE, true, new ItemConfig.Item(
//            List.of(
//                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.SOUL).id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
//                    new ItemConfig.AttributeModifier(SpellAttributes.HASTE.id, tier_3_secondary_multiplier * 2, EntityAttributeModifier.Operation.MULTIPLY_BASE)
//            )
//    ));
//
//    public static Entry unique_soul_necklace = add(new Identifier(JewelryMod.ID, "unique_soul_necklace"), Rarity.RARE, true, new ItemConfig.Item(
//            List.of(
//                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.SOUL).id, tier_3_spell_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE),
//                    new ItemConfig.AttributeModifier(SpellAttributes.HASTE.id, tier_3_secondary_multiplier * 2, EntityAttributeModifier.Operation.MULTIPLY_BASE)
//            )
//    ));

    public static void register(ItemConfig allConfigs) {
        for (var entry : all) {
            ItemConfig.Item itemConfig = allConfigs.items.get(entry.id.toString());
            if (itemConfig == null) {
                itemConfig = entry.config;
                allConfigs.items.put(entry.id.toString(), entry.config);
            }

            var modifiers = new ArrayList<JewelryItem.Modifier>();
            for (var modifier : itemConfig.attributes) {
                var attribute = AttributeResolver.get(new Identifier(modifier.id));
                if (attribute == null) {
                    System.err.println("Failed to resolve EntityAttribute with id: " + modifier.id);
                    continue;
                }
                modifiers.add(new JewelryItem.Modifier(
                        attribute,
                        "Jewelry modifier",
                        modifier.value,
                        modifier.operation));
            }
            entry.item().setConfigurableModifiers(modifiers);
            Registry.register(Registry.ITEM, entry.id(), entry.item());
        }
    }

    private static final UUID placeHolderUUID = UUID.randomUUID();
}
