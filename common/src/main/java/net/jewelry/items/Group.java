package net.jewelry.items;

import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class Group {
    public static Identifier ID = Identifier.fromNamespaceAndPath(JewelryMod.ID, "generic");
    public static ResourceKey<CreativeModeTab> KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ID);
    // Vanilla ItemGroup.Builder — FabricItemGroup.builder() is Fabric-API-only and this static
    // initializer runs on both loaders (the group is created in common, registered from registerItems).
    public static CreativeModeTab JEWELRY = new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> {
                var item = BuiltInRegistries.ITEM.get(JewelryItems.ruby_ring.id()).get().value();
                return new ItemStack(item);
            })
            // `.generic` suffix is required by older versions, keeping it for translation consistency
            .title(Component.translatable("itemGroup." + JewelryMod.ID + ".generic"))
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
