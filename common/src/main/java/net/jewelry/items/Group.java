package net.jewelry.items;

import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Group {
    public static Identifier ID = Identifier.of(JewelryMod.ID, "generic");
    public static RegistryKey<ItemGroup> KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), ID);
    // Vanilla ItemGroup.Builder — FabricItemGroup.builder() is Fabric-API-only and this static
    // initializer runs on both loaders (the group is created in common, registered from registerItems).
    public static ItemGroup JEWELRY = new ItemGroup.Builder(ItemGroup.Row.TOP, 0)
            .icon(() -> {
                var item = Registries.ITEM.getEntry(JewelryItems.ruby_ring.id()).get().value();
                return new ItemStack(item);
            })
            // `.generic` suffix is required by older versions, keeping it for translation consistency
            .displayName(Text.translatable("itemGroup." + JewelryMod.ID + ".generic"))
            .build();

    /// The creative-tab contents in display order: block items first (gem veins, jeweler's kit),
    /// then the raw gems, then the jewelry items. Both loader entrypoints iterate this single list
    /// (Fabric `ItemGroupEvents`, NeoForge `BuildCreativeModeTabContentsEvent`), so the tab order is
    /// identical on both. Called at event time, when every `all` list is already populated.
    public static List<Item> orderedEntries() {
        var entries = new ArrayList<Item>();
        for (var entry : JewelryBlocks.all) {
            entries.add(entry.item());
        }
        for (var entry : Gems.all) {
            entries.add(entry.item());
        }
        for (var entry : JewelryItems.all) {
            entries.add(entry.item());
        }
        return entries;
    }
}
