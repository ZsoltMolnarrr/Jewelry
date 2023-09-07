package net.jewelry.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.jewelry.JewelryMod;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;

public class Gems {
    public record Entry(Identifier id, Item item) { }
    public static ArrayList<Entry> all = new ArrayList<>();
    public static Entry gem(Identifier id) {
        var entry = new Entry(id, new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).group(Group.JEWELRY)));
        all.add(entry);
        return entry;
    }

    public static final Entry ruby = gem(new Identifier(JewelryMod.ID, "ruby"));
    public static final Entry topaz = gem(new Identifier(JewelryMod.ID, "topaz"));
    public static final Entry citrine = gem(new Identifier(JewelryMod.ID, "citrine"));
    public static final Entry jade = gem(new Identifier(JewelryMod.ID, "jade"));
    public static final Entry sapphire = gem(new Identifier(JewelryMod.ID, "sapphire"));
    public static final Entry tanzanite = gem(new Identifier(JewelryMod.ID, "tanzanite"));

    public static void register() {
        for (var entry : all) {
            Registry.register(Registry.ITEM, entry.id(), entry.item());
        }
    }
}
