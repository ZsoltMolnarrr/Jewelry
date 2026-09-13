package net.jewelry.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.jewelry.gems.GemCutRegistry;
import net.jewelry.gems.GemCuts;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

/// Emits the cuts declared in [GemCuts] as `data/jewelry/gem_cut/<id>.json`.
/// The `gem_cut` registry is known to datagen because mod init registers it as a synced dynamic
/// registry before providers run (same as Armory's equipment-set generator).
public class GemCutGenerator extends FabricDynamicRegistryProvider {
    public GemCutGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        for (var cut : GemCuts.all) {
            entries.add(RegistryKey.of(GemCutRegistry.KEY, cut.id()), cut.definition());
        }
    }

    @Override
    public String getName() {
        return "Jewelry Gem Cuts";
    }
}
