package net.jewelry.items;

import net.jewelry.JewelryMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import java.util.ArrayList;

public class Gems {
    public record Entry(Identifier id, Item item) { }
    public static ArrayList<Entry> all = new ArrayList<>();
    public static Entry gem(Identifier id) {
        // 1.21.2+: settings must carry the item's own registry key or the constructor throws `Item id not set`.
        var entry = new Entry(id, new Item(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .rarity(Rarity.UNCOMMON)));
        all.add(entry);
        return entry;
    }

    public static final Entry ruby = gem(Identifier.fromNamespaceAndPath(JewelryMod.ID, "ruby"));
    public static final Entry topaz = gem(Identifier.fromNamespaceAndPath(JewelryMod.ID, "topaz"));
    public static final Entry citrine = gem(Identifier.fromNamespaceAndPath(JewelryMod.ID, "citrine"));
    public static final Entry jade = gem(Identifier.fromNamespaceAndPath(JewelryMod.ID, "jade"));
    public static final Entry sapphire = gem(Identifier.fromNamespaceAndPath(JewelryMod.ID, "sapphire"));
    public static final Entry tanzanite = gem(Identifier.fromNamespaceAndPath(JewelryMod.ID, "tanzanite"));

    public static void register() {
        for (var entry : all) {
            Registry.register(BuiltInRegistries.ITEM, entry.id(), entry.item());
        }
        // Creative-tab placement: see `Group.orderedEntries` (single ordered list for both loaders).
    }
}
