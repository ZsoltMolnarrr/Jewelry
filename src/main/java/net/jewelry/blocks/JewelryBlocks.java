package net.jewelry.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.jewelry.JewelryMod;
import net.jewelry.items.Group;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.OreBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;

public class JewelryBlocks {

    public record Entry(String name, Block block, BlockItem item) {
        public Entry(String name, Block block) {
            this(name, block, new BlockItem(block, new FabricItemSettings().group(Group.JEWELRY)));
        }
    }

    public static final ArrayList<Entry> all = new ArrayList<>();

    private static Entry entry(String name, Block block) {
        var entry = new Entry(name, block);
        all.add(entry);
        return entry;
    }

    public static final Entry GEM_VEIN = entry("gem_vein", new OreBlock(
            FabricBlockSettings.of(Material.STONE)
                .mapColor(MapColor.STONE_GRAY)
                .requiresTool()
                .strength(3.0F, 3.0F),
            UniformIntProvider.create(3, 7)
    ));

    public static final Entry DEEPSLATE_GEM_VEIN = entry("deepslate_gem_vein", new OreBlock(
            FabricBlockSettings.of(Material.STONE)
                    .mapColor(MapColor.STONE_GRAY)
                    .requiresTool()
                    .strength(3.0F, 3.0F),
            UniformIntProvider.create(3, 7)
    ));

    public static final Entry JEWELERS_KIT = entry("jewelers_kit", new JewelersKitBlock(
            FabricBlockSettings.of(Material.REPAIR_STATION)
                    .mapColor(MapColor.OAK_TAN)
                    .strength(2.5F)
                    .sounds(BlockSoundGroup.WOOD)
                    .nonOpaque()
    ));

    public static void register() {
        for (var entry : all) {
            Registry.register(Registry.BLOCK, new Identifier(JewelryMod.ID, entry.name), entry.block);
            Registry.register(Registry.ITEM, new Identifier(JewelryMod.ID, entry.name), entry.item());
        }
    }
}
