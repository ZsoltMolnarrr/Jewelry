package net.jewelry.items;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.jewelry.JewelryMod;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.ArrayList;

public class Gems {
    public record Entry(Identifier id, Item item) { }
    public static ArrayList<Entry> all = new ArrayList<>();
    public static Entry gem(Identifier id) {
        var entry = new Entry(id, new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));
        all.add(entry);
        return entry;
    }

    public static final Entry ruby = gem(Identifier.of(JewelryMod.ID, "ruby"));
    public static final Entry topaz = gem(Identifier.of(JewelryMod.ID, "topaz"));
    public static final Entry citrine = gem(Identifier.of(JewelryMod.ID, "citrine"));
    public static final Entry jade = gem(Identifier.of(JewelryMod.ID, "jade"));
    public static final Entry sapphire = gem(Identifier.of(JewelryMod.ID, "sapphire"));
    public static final Entry tanzanite = gem(Identifier.of(JewelryMod.ID, "tanzanite"));

    public static void register() {
        for (var entry : all) {
            Registry.register(Registries.ITEM, entry.id(), entry.item());
        }

        ItemGroupEvents.modifyEntriesEvent(Group.KEY).register((content) -> {
            for (var entry : all) {
                content.add(entry.item());
            }
        });
    }
}
