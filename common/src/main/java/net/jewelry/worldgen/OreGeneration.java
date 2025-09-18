package net.jewelry.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.jewelry.JewelryMod;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.*;

public class OreGeneration {
    public static final Identifier GEM_VEIN_ID = Identifier.of(JewelryMod.ID, "gem_vein_placed");
    public static final RegistryKey<PlacedFeature> GEM_VEIN_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(JewelryMod.ID, "gem_vein_placed"));

    public static void register() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                GEM_VEIN_PLACED_KEY
        );
    }
}
