package net.jewelry.blocks;

import net.jewelry.JewelryMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import java.util.ArrayList;
import java.util.function.Function;

public class JewelryBlocks {

    public record Entry(String name, Block block, BlockItem item) { }

    public static final ArrayList<Entry> all = new ArrayList<>();

    /// 1.21.2+ requires every `AbstractBlock.Settings` / `Item.Settings` to carry its own
    /// `registryKey` (the game crashes with `Block id not set` on the first construction otherwise),
    /// so blocks are built from a factory that receives settings already keyed by their id.
    private static Entry entry(String name, Function<BlockBehaviour.Properties, Block> blockFactory) {
        return entry(name, blockFactory, null);
    }

    private static Entry entry(String name, Function<BlockBehaviour.Properties, Block> blockFactory, String hint) {
        var id = Identifier.fromNamespaceAndPath(JewelryMod.ID, name);
        var block = blockFactory.apply(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id)));
        var itemSettings = new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .useBlockDescriptionPrefix();
        var entry = new Entry(name, block, new JewelryBlockItem(block, itemSettings, hint));
        all.add(entry);
        return entry;
    }

    public static final Entry GEM_VEIN = entry("gem_vein", settings ->
            new DropExperienceBlock(UniformInt.of(3, 7), settings
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(3.0F, 3.0F)
    ));

    public static final Entry DEEPSLATE_GEM_VEIN = entry("deepslate_gem_vein", settings ->
            new DropExperienceBlock(UniformInt.of(3, 7), settings
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                // DeepSlate specific settings
                .mapColor(MapColor.DEEPSLATE)
                .sound(SoundType.DEEPSLATE)
                .strength(4.5F, 3.0F)
    ));

    public static final Entry JEWELERS_KIT = entry("jewelers_kit", settings ->
            new JewelersKitBlock(settings
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.5F)
                .sound(SoundType.WOOD)
                .noOcclusion()
    ), "block.jewelry.jewelers_kit.hint");

    public static void register() {
        for (var entry : all) {
            Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(JewelryMod.ID, entry.name), entry.block);
            Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(JewelryMod.ID, entry.name), entry.item());
        }
        // Creative-tab placement: see `Group.orderedEntries` (blocks come first in the tab).
    }
}
