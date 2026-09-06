package net.jewelry.blocks;

import net.jewelry.JewelryMod;
import net.jewelry.items.Group;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.ArrayList;

public class JewelryBlocks {

    public record Entry(String name, Block block, BlockItem item) {
        public Entry(String name, Block block) {
            this(name, block, new BlockItem(block, new Item.Settings()));
        }
    }

    public static final ArrayList<Entry> all = new ArrayList<>();

    private static Entry entry(String name, Block block) {
        var entry = new Entry(name, block);
        all.add(entry);
        return entry;
    }

    // 1.20.1 `ExperienceDroppingBlock` takes (Settings, IntProvider); 1.21 swapped the argument order.
    public static final Entry GEM_VEIN = entry("gem_vein", new ExperienceDroppingBlock(
            AbstractBlock.Settings.create()
                .mapColor(MapColor.STONE_GRAY)
                .instrument(Instrument.BASEDRUM)
                .requiresTool()
                .strength(3.0F, 3.0F),
            UniformIntProvider.create(3, 7)
    ));

    public static final Entry DEEPSLATE_GEM_VEIN = entry("deepslate_gem_vein", new ExperienceDroppingBlock(
            AbstractBlock.Settings.create()
                .instrument(Instrument.BASEDRUM)
                .requiresTool()
                // DeepSlate specific settings
                .mapColor(MapColor.DEEPSLATE_GRAY)
                .sounds(BlockSoundGroup.DEEPSLATE)
                .strength(4.5F, 3.0F),
            UniformIntProvider.create(3, 7)
    ));

    public static final Entry JEWELERS_KIT = entry("jewelers_kit", new JewelersKitBlock(
            AbstractBlock.Settings.create()
                .mapColor(MapColor.OAK_TAN)
                .instrument(Instrument.BASS)
                .strength(2.5F)
                .sounds(BlockSoundGroup.WOOD)
                .nonOpaque()
    ));

    /// Blocks only. Forge 47 unfreezes exactly one registry per `RegisterEvent` window, so the
    /// `BlockItem`s are registered separately from {@link #registerBlockItems()} (called from the ITEM
    /// window); registering both here crashes the Forge boot with "Can not register to a locked registry".
    public static void register() {
        for (var entry : all) {
            Registry.register(Registries.BLOCK, new Identifier(JewelryMod.ID, entry.name), entry.block);
        }
    }

    public static void registerBlockItems() {
        for (var entry : all) {
            Registry.register(Registries.ITEM, new Identifier(JewelryMod.ID, entry.name), entry.item());
        }
        // Creative-tab placement is registered per-platform from each loader's entrypoint (iterating JewelryBlocks.all).
    }
}
