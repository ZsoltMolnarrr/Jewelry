package net.jewelry.fabric.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.jewelry.JewelryMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class OreGeneration {
    public static final Identifier GEM_VEIN_ID = Identifier.fromNamespaceAndPath(JewelryMod.ID, "gem_vein_placed");
    public static final ResourceKey<PlacedFeature> GEM_VEIN_PLACED_KEY = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(JewelryMod.ID, "gem_vein_placed"));

    public static void register() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                GEM_VEIN_PLACED_KEY
        );
    }
}
