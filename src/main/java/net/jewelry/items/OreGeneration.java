package net.jewelry.items;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.jewelry.JewelryMod;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.*;

public class OreGeneration {
    public static final Identifier GEM_VEIN_ID = new Identifier(JewelryMod.ID, "gem_vein_placed");
//    public static class ConfiguredFeatures {
//        public static final RegistryKey<ConfiguredFeature<?, ?>> GEM_VEIN_KEY = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, GEM_VEIN_ID);
//
//        public static void register(Registerable<ConfiguredFeature<?, ?>> context) {
//            List<OreFeatureConfig.Target> overworldGemVeins =
//                    List.of(OreFeatureConfig.createTarget(
//                                    new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES),
//                                    JewelryBlocks.GEM_VEIN.block().getDefaultState()
//                            ),
//                            OreFeatureConfig.createTarget(
//                                    new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES),
//                                    JewelryBlocks.DEEPSLATE_GEM_VEIN.block().getDefaultState()
//                            )
//                    );
//
//            register(context, GEM_VEIN_KEY, Feature.ORE, new OreFeatureConfig(overworldGemVeins, 4));
//        }
//
//        private static <FC extends FeatureConfig, F extends Feature<FC>> void register(
//                Registerable<ConfiguredFeature<?, ?>> context,
//                RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
//            context.register(key, new ConfiguredFeature<>(feature, configuration));
//        }
//    }
//
//    public static class PlacedFeatures {
//        public static final RegistryKey<PlacedFeature> GEM_VEIN_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, GEM_VEIN_ID);
//
//        public static void register(Registerable<PlacedFeature> context) {
//            var configuredFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
//
//            register(context, GEM_VEIN_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(ConfiguredFeatures.GEM_VEIN_KEY),
//                    modifiersWithCount(16, // Veins per Chunk
//                            HeightRangePlacementModifier.uniform(YOffset.fixed(-80), YOffset.fixed(80))));
//        }
//
//        private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration,
//                                     List<PlacementModifier> modifiers) {
//            context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
//        }
//
//        private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
//                                                                                       RegistryEntry<ConfiguredFeature<?, ?>> configuration,
//                                                                                       PlacementModifier... modifiers) {
//            register(context, key, configuration, List.of(modifiers));
//        }
//
//        public static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
//            return List.of(countModifier, SquarePlacementModifier.of(), heightModifier, BiomePlacementModifier.of());
//        }
//
//        public static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModifier) {
//            return modifiers(CountPlacementModifier.of(count), heightModifier);
//        }
//
//        public static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier) {
//            return modifiers(RarityFilterPlacementModifier.of(chance), heightModifier);
//        }
//    }

    public static final RegistryKey<PlacedFeature> GEM_VEIN_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(JewelryMod.ID, "gem_vein_placed"));

    public static void register() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                GEM_VEIN_PLACED_KEY
        );
    }
}
