package net.jewelry.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.jewelry.JewelryMod;
import net.jewelry.api.AttributeResolver;
import net.jewelry.api.JewelryItem;
import net.jewelry.config.ItemConfig;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
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

    public static Entry add(Identifier id, Rarity rarity, ItemConfig.Item config, boolean addLore) {
        return add(id, rarity, config, addLore ? ("item." + id.getNamespace() + "." + id.getPath() + ".lore") : null);
    }

    public static Entry add(Identifier id, Rarity rarity, ItemConfig.Item config, String lore) {
        var entry = new Entry(id, new JewelryItem(new FabricItemSettings().rarity(rarity), lore), config);
        all.add(entry);
        entryMap.put(id.toString(), entry.item());
        return entry;
    }

    private static final float tier_1_multiplier = 0.04F;
    private static final float tier_2_multiplier = 0.08F;

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
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry citrine_ring = add(new Identifier(JewelryMod.ID, "citrine_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, 1, EntityAttributeModifier.Operation.ADDITION)
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
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FROST).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.SOUL).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    // MARK: Custom gem necklaces

    public static Entry ruby_necklace = add(new Identifier(JewelryMod.ID, "ruby_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("generic.attack_damage", tier_1_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    // sunfire
    public static Entry topaz_necklace = add(new Identifier(JewelryMod.ID, "topaz_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry citrine_necklace = add(new Identifier(JewelryMod.ID, "citrine_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, 1, EntityAttributeModifier.Operation.ADDITION)
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
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FROST).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.SOUL).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));


    // MARK: Netherite variants

    public static Entry netherite_ruby_ring = add(new Identifier(JewelryMod.ID, "netherite_ruby_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.attack_damage", tier_2_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    public static Entry netherite_topaz_ring = add(new Identifier(JewelryMod.ID, "netherite_topaz_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, 2, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_citrine_ring = add(new Identifier(JewelryMod.ID, "netherite_citrine_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, 2, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_jade_ring = add(new Identifier(JewelryMod.ID, "netherite_jade_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(EntityAttributes_ProjectileDamage.attributeId, tier_2_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    public static Entry netherite_sapphire_ring = add(new Identifier(JewelryMod.ID, "netherite_sapphire_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.max_health", 4, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_tanzanite_ring = add(new Identifier(JewelryMod.ID, "netherite_tanzanite_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FROST).id, 2, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.SOUL).id, 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_ruby_necklace = add(new Identifier(JewelryMod.ID, "netherite_ruby_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("generic.attack_damage", tier_2_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    public static Entry netherite_topaz_necklace = add(new Identifier(JewelryMod.ID, "netherite_topaz_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, 2, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_citrine_necklace = add(new Identifier(JewelryMod.ID, "netherite_citrine_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, 2, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_jade_necklace = add(new Identifier(JewelryMod.ID, "netherite_jade_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(EntityAttributes_ProjectileDamage.attributeId, tier_2_multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE)
            )
    ));

    public static Entry netherite_sapphire_necklace = add(new Identifier(JewelryMod.ID, "netherite_sapphire_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.max_health", 4, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_tanzanite_necklace = add(new Identifier(JewelryMod.ID, "netherite_tanzanite_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FROST).id, 2, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.SOUL).id, 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));


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
            Registry.register(Registries.ITEM, entry.id(), entry.item());
        }

        ItemGroupEvents.modifyEntriesEvent(Group.KEY).register((content) -> {
            for (var entry : all) {
                content.add(entry.item());
            }
        });
    }

    private static final UUID placeHolderUUID = UUID.randomUUID();
}
