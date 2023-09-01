package net.jewelry.internals;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.jewelry.JewelryMod;
import net.jewelry.api.AttributeResolver;
import net.jewelry.config.ItemConfig;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
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

    public static Entry bold_ring = add(new Identifier(JewelryMod.ID, "bold_ring"), new ItemConfig.Item(
            List.of(
                    new ItemConfig.AttributeModifier("generic.attack_damage", 1, EntityAttributeModifier.Operation.ADDITION)
            )
    ));

    public static void register(ItemConfig config) {
        for (var entry : all) {
            ItemConfig.Item itemConfig = config.items.get(entry.id.toString());
            if (itemConfig == null) {
                itemConfig = entry.config;
            } else {
                config.items.put(entry.id.toString(), entry.config);
            }

            var modifiers = new HashMap<EntityAttribute, EntityAttributeModifier>();
            for (var attribute : itemConfig.attributes) {
                modifiers.put(AttributeResolver.get(new Identifier(attribute.id)),
                        new EntityAttributeModifier(placeHolderUUID, "Jewelry Attribute", attribute.value, attribute.operation));
            }
            entry.item().setModifiers(modifiers);

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
