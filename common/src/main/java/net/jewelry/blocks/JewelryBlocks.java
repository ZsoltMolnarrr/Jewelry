package net.jewelry.blocks;

import net.jewelry.JewelryMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.ArrayList;
import java.util.function.Function;

public class JewelryBlocks {

    public record Entry(String name, Block block, BlockItem item) { }

    public static final ArrayList<Entry> all = new ArrayList<>();

    /// 1.21.2+ requires every `AbstractBlock.Settings` / `Item.Settings` to carry its own
    /// `registryKey` (the game crashes with `Block id not set` on the first construction otherwise),
    /// so blocks are built from a factory that receives settings already keyed by their id.
    private static Entry entry(String name, Function<AbstractBlock.Settings, Block> blockFactory) {
        return entry(name, blockFactory, null);
    }

    private static Entry entry(String name, Function<AbstractBlock.Settings, Block> blockFactory, String hint) {
        var id = Identifier.of(JewelryMod.ID, name);
        var block = blockFactory.apply(AbstractBlock.Settings.create()
                .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id)));
        var itemSettings = new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, id))
                .useBlockPrefixedTranslationKey();
        var entry = new Entry(name, block, new JewelryBlockItem(block, itemSettings, hint));
        all.add(entry);
        return entry;
    }

    public static final Entry GEM_VEIN = entry("gem_vein", settings ->
            new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), settings
                .mapColor(MapColor.STONE_GRAY)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresTool()
                .strength(3.0F, 3.0F)
    ));

    public static final Entry DEEPSLATE_GEM_VEIN = entry("deepslate_gem_vein", settings ->
            new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), settings
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresTool()
                // DeepSlate specific settings
                .mapColor(MapColor.DEEPSLATE_GRAY)
                .sounds(BlockSoundGroup.DEEPSLATE)
                .strength(4.5F, 3.0F)
    ));

    public static final Entry JEWELERS_KIT = entry("jewelers_kit", settings ->
            new JewelersKitBlock(settings
                .mapColor(MapColor.OAK_TAN)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.5F)
                .sounds(BlockSoundGroup.WOOD)
                .nonOpaque()
    ), "block.jewelry.jewelers_kit.hint");

    public static void register() {
        for (var entry : all) {
            Registry.register(Registries.BLOCK, Identifier.of(JewelryMod.ID, entry.name), entry.block);
            Registry.register(Registries.ITEM, Identifier.of(JewelryMod.ID, entry.name), entry.item());
        }
        // Creative-tab placement is registered per-platform from each loader's entrypoint (iterating JewelryBlocks.all).
    }
}
