package net.jewelry.internals;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.jewelry.JewelryMod;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.ArrayList;

public class JewelryBlocks {

    public record Entry(String name, Block block, BlockItem item) {
        public Entry(String name, Block block) {
            this(name, block, new BlockItem(block, new FabricItemSettings()));
        }
    }

    public static final ArrayList<Entry> all = new ArrayList<>();

    private static Entry entry(String name, Block block) {
        var entry = new Entry(name, block);
        all.add(entry);
        return entry;
    }

    public static final Entry GEM_VEIN = entry("gem_vein", new ExperienceDroppingBlock(
            FabricBlockSettings.create()
                .mapColor(MapColor.STONE_GRAY)
                .instrument(Instrument.BASEDRUM)
                .requiresTool()
                .strength(3.0F, 3.0F),
            UniformIntProvider.create(3, 7)
    ));

    public static final Entry DEEPSLATE_GEM_VEIN = entry("deepslate_gem_vein", new ExperienceDroppingBlock(
            FabricBlockSettings.create()
                    .mapColor(MapColor.STONE_GRAY)
                    .instrument(Instrument.BASEDRUM)
                    .requiresTool()
                    .strength(3.0F, 3.0F),
            UniformIntProvider.create(3, 7)
    ));

    public static void register() {
        for (var entry : all) {
            Registry.register(Registries.BLOCK, new Identifier(JewelryMod.ID, entry.name), entry.block);
            Registry.register(Registries.ITEM, new Identifier(JewelryMod.ID, entry.name), entry.item());
        }
        ItemGroupEvents.modifyEntriesEvent(Group.KEY).register((content) -> {
            for (var entry : all) {
                content.add(entry.item());
            }
        });
    }
}
