package net.jewelry.internals;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.jewelry.JewelryMod;
import net.jewelry.api.AttributeResolver;
import net.jewelry.config.ItemConfig;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.projectile_damage.api.EntityAttributes_ProjectileDamage;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.SpellAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JewelryItems {
    public record Entry(Identifier id, RingItem item, ItemConfig.Item config) {  }
    public static ArrayList<Entry> all = new ArrayList<>();
    public static Entry add(Identifier id, ItemConfig.Item config) {
        var entry = new Entry(id, new RingItem(new FabricItemSettings()), config);
        all.add(entry);
        return entry;
    }

    // MARK: Rings

    // bold
    public static Entry red_ring = add(new Identifier(JewelryMod.ID, "red_ring"), new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("minecraft:generic.attack_damage", 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    // sunfire
    public static Entry orange_ring = add(new Identifier(JewelryMod.ID, "orange_ring"), new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry yellow_ring = add(new Identifier(JewelryMod.ID, "yellow_ring"), new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    // delicate
    public static Entry green_ring = add(new Identifier(JewelryMod.ID, "green_ring"), new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(EntityAttributes_ProjectileDamage.attributeId, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry blue_ring = add(new Identifier(JewelryMod.ID, "blue_ring"), new ItemConfig.Item(
            List.of(
            )
    ));

    public static Entry purple_ring = add(new Identifier(JewelryMod.ID, "purple_ring"), new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FROST).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.SOUL).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    // MARK: Amulets

    public static Entry red_necklace = add(new Identifier(JewelryMod.ID, "red_necklace"), new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("generic.attack_damage", 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    // sunfire
    public static Entry orange_necklace = add(new Identifier(JewelryMod.ID, "orange_necklace"), new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.ARCANE).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry yellow_necklace = add(new Identifier(JewelryMod.ID, "yellow_necklace"), new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.HEALING).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.LIGHTNING).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    // delicate
    public static Entry green_necklace = add(new Identifier(JewelryMod.ID, "green_necklace"), new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(EntityAttributes_ProjectileDamage.attributeId, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static Entry blue_necklace = add(new Identifier(JewelryMod.ID, "blue_necklace"), new ItemConfig.Item(
            List.of(
            )
    ));

    public static Entry purple_necklace = add(new Identifier(JewelryMod.ID, "purple_necklace"), new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.FROST).id, 1, EntityAttributeModifier.Operation.ADDITION),
                    new ItemConfig.AttributeModifier(SpellAttributes.POWER.get(MagicSchool.SOUL).id, 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static void register(ItemConfig allConfigs) {
        for (var entry : all) {
            ItemConfig.Item itemConfig = allConfigs.items.get(entry.id.toString());
            if (itemConfig == null) {
                itemConfig = entry.config;
                allConfigs.items.put(entry.id.toString(), entry.config);
            }

            var modifiers = new ArrayList<RingItem.Modifier>();
            for (var modifier : itemConfig.attributes) {
                var attribute = AttributeResolver.get(new Identifier(modifier.id));
                if (attribute == null) {
                    System.err.println("Failed to resolve EntityAttribute with id: " + modifier.id);
                    continue;
                }
                modifiers.add(new RingItem.Modifier(
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
