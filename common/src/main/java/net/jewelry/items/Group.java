package net.jewelry.items;

import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.gems.GemCut;
import net.jewelry.gems.GemCutRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Comparator;

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

    public static Identifier GEMS_ID = Identifier.of(JewelryMod.ID, "gems");
    public static RegistryKey<ItemGroup> GEMS_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), GEMS_ID);
    /// The gem blocks (veins, Jeweler's Kit), then raw gems, then every cut variant enumerated from the `gem_cut` registry at display time
    /// (the same way vanilla lists enchanted books), so data-pack cuts show up without code changes.
    /// Built with the vanilla `entries` collector, which both loaders honour, so no per-platform code.
    public static ItemGroup GEMS = new ItemGroup.Builder(ItemGroup.Row.TOP, 0)
            .icon(() -> new ItemStack(Gems.ruby.item()))
            .displayName(Text.translatable("itemGroup." + JewelryMod.ID + ".gems"))
            .entries((context, entries) -> {
                for (var block : JewelryBlocks.all) {
                    entries.add(block.item());
                }
                for (var gem : Gems.all) {
                    entries.add(gem.item());
                }
                GemCutRegistry.from(context.lookup()).ifPresent(registry -> {
                    // Every cut in the registry, other mods' included: grouped by gem — Jewelry's gems in
                    // declaration order, then any other gem by item id — and cuts of a gem in id order.
                    var jewelryGems = Gems.all.stream().map(Gems.Entry::item).toList();
                    Comparator<RegistryEntry.Reference<GemCut>> byGem = Comparator.comparing(entry -> {
                        var gem = entry.value().gem().value();
                        int index = jewelryGems.indexOf(gem);
                        return index >= 0 ? String.format("0%03d", index) : "1" + Registries.ITEM.getId(gem);
                    });
                    registry.streamEntries()
                            .sorted(byGem.thenComparing(entry -> entry.registryKey().getValue()))
                            .forEach(entry -> entries.add(GemCut.stack(entry)));
                });
            })
            .build();
}
