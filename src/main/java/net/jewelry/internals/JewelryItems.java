package net.jewelry.internals;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.jewelry.JewelryMod;
import net.jewelry.api.AttributeResolver;
import net.jewelry.api.JewelryItem;
import net.jewelry.config.ItemConfig;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.projectile_damage.api.EntityAttributes_ProjectileDamage;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.SpellAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JewelryItems {
    public record Entry(Identifier id, JewelryItem item, ItemConfig.Item config) {  }
    public static ArrayList<Entry> all = new ArrayList<>();

    public static Entry add(Identifier id, ItemConfig.Item config) {
        return add(id, Rarity.COMMON, config);
    }

    public static Entry add(Identifier id, Rarity rarity, ItemConfig.Item config) {
        var entry = new Entry(id, new JewelryItem(new FabricItemSettings().rarity(rarity)), config);
        all.add(entry);
        return entry;
    }

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
    public static Entry red_ring = add(new Identifier(JewelryMod.ID, "red_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.attack_damage", 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    // sunfire
    public static Entry orange_ring = add(new Identifier(JewelryMod.ID, "orange_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry yellow_ring = add(new Identifier(JewelryMod.ID, "yellow_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    // delicate
    public static Entry green_ring = add(new Identifier(JewelryMod.ID, "green_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(EntityAttributes_ProjectileDamage.attributeId, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry blue_ring = add(new Identifier(JewelryMod.ID, "blue_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.max_health", 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry purple_ring = add(new Identifier(JewelryMod.ID, "purple_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FROST).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.SOUL).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    // MARK: Custom gen necklaces

    public static Entry red_necklace = add(new Identifier(JewelryMod.ID, "red_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("generic.attack_damage", 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    // sunfire
    public static Entry orange_necklace = add(new Identifier(JewelryMod.ID, "orange_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry yellow_necklace = add(new Identifier(JewelryMod.ID, "yellow_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    // delicate
    public static Entry green_necklace = add(new Identifier(JewelryMod.ID, "green_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(EntityAttributes_ProjectileDamage.attributeId, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry blue_necklace = add(new Identifier(JewelryMod.ID, "blue_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.max_health", 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry purple_necklace = add(new Identifier(JewelryMod.ID, "purple_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FROST).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.SOUL).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));


    // MARK: Netherite variants

    public static Entry netherite_red_ring = add(new Identifier(JewelryMod.ID, "netherite_red_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.attack_damage", 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_orange_ring = add(new Identifier(JewelryMod.ID, "netherite_orange_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, 2, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_yellow_ring = add(new Identifier(JewelryMod.ID, "netherite_yellow_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, 2, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_green_ring = add(new Identifier(JewelryMod.ID, "netherite_green_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(EntityAttributes_ProjectileDamage.attributeId, 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_blue_ring = add(new Identifier(JewelryMod.ID, "netherite_blue_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.max_health", 4, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_purple_ring = add(new Identifier(JewelryMod.ID, "netherite_purple_ring"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FROST).id, 2, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.SOUL).id, 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_red_necklace = add(new Identifier(JewelryMod.ID, "netherite_red_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("generic.attack_damage", 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_orange_necklace = add(new Identifier(JewelryMod.ID, "netherite_orange_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, 2, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_yellow_necklace = add(new Identifier(JewelryMod.ID, "netherite_yellow_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, 2, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_green_necklace = add(new Identifier(JewelryMod.ID, "netherite_green_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(EntityAttributes_ProjectileDamage.attributeId, 2, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_blue_necklace = add(new Identifier(JewelryMod.ID, "netherite_blue_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.max_health", 4, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry netherite_purple_necklace = add(new Identifier(JewelryMod.ID, "netherite_purple_necklace"), Rarity.UNCOMMON, new ItemConfig.Item(
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
                        "Jewelry Attribute",
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
