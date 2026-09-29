package net.jewelry.items;

import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.gems.GemCut;
import net.jewelry.gems.GemCutRegistry;
import net.jewelry.gems.SocketMounts;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import java.util.Comparator;

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

    public static Identifier GEMS_ID = Identifier.fromNamespaceAndPath(JewelryMod.ID, "gems");
    public static ResourceKey<CreativeModeTab> GEMS_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), GEMS_ID);
    /// The gem blocks (veins, Jeweler's Kit), then raw gems, then every cut variant enumerated from the `gem_cut` registry at display time
    /// (the same way vanilla lists enchanted books), so data-pack cuts show up without code changes.
    /// Built with the vanilla `entries` collector, which both loaders honour, so no per-platform code.
    public static CreativeModeTab GEMS = new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(Gems.ruby.item()))
            .title(Component.translatable("itemGroup." + JewelryMod.ID + ".gems"))
            .displayItems((context, entries) -> {
                for (var block : JewelryBlocks.all) {
                    entries.accept(block.item());
                }
                for (var mount : SocketMounts.all) {
                    entries.accept(mount.item());
                }
                for (var gem : Gems.all) {
                    entries.accept(gem.item());
                }
                GemCutRegistry.from(context.holders()).ifPresent(registry -> {
                    // Every cut in the registry, other mods' included: grouped by gem — Jewelry's gems in
                    // declaration order, then any other gem by item id — and cuts of a gem in id order.
                    var jewelryGems = Gems.all.stream().map(Gems.Entry::item).toList();
                    Comparator<Holder.Reference<GemCut>> byGem = Comparator.comparing(entry -> {
                        var gem = entry.value().gem().value();
                        int index = jewelryGems.indexOf(gem);
                        return index >= 0 ? String.format("0%03d", index) : "1" + BuiltInRegistries.ITEM.getKey(gem);
                    });
                    registry.listElements()
                            .sorted(byGem.thenComparing(entry -> entry.key().identifier()))
                            .forEach(entry -> entries.accept(GemCut.stack(entry)));
                });
            })
            .build();
}
