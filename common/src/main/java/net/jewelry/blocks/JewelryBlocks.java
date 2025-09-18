package net.jewelry.blocks;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.jewelry.JewelryMod;
import net.jewelry.items.Group;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
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

    public static final Entry GEM_VEIN = entry("gem_vein", new ExperienceDroppingBlock(UniformIntProvider.create(3, 7),
            AbstractBlock.Settings.create()
                .mapColor(MapColor.STONE_GRAY)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresTool()
                .strength(3.0F, 3.0F)
    ));

    public static final Entry DEEPSLATE_GEM_VEIN = entry("deepslate_gem_vein", new ExperienceDroppingBlock(UniformIntProvider.create(3, 7),
            AbstractBlock.Settings.create()
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresTool()
                // DeepSlate specific settings
                .mapColor(MapColor.DEEPSLATE_GRAY)
                .sounds(BlockSoundGroup.DEEPSLATE)
                .strength(4.5F, 3.0F)
    ));

    public static final Entry JEWELERS_KIT = entry("jewelers_kit", new JewelersKitBlock(
            AbstractBlock.Settings.create()
                .mapColor(MapColor.OAK_TAN)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.5F)
                .sounds(BlockSoundGroup.WOOD)
                .nonOpaque()
    ));

    public static void register() {
        for (var entry : all) {
            Registry.register(Registries.BLOCK, Identifier.of(JewelryMod.ID, entry.name), entry.block);
            Registry.register(Registries.ITEM, Identifier.of(JewelryMod.ID, entry.name), entry.item());
        }
        ItemGroupEvents.modifyEntriesEvent(Group.KEY).register((content) -> {
            for (var entry : all) {
                content.add(entry.item());
            }
        });
    }
}
