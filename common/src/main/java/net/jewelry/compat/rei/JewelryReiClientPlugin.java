package net.jewelry.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.gems.GemCut;
import net.jewelry.gems.GemCutRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * Surfaces gem cuts — entries of the synced {@code gem_cut} datapack registry, which are not recipes at
 * all as far as vanilla (and therefore REI) is concerned — under their own category, worked at the
 * Jeweler's Kit. The REI counterpart of the EMI plugin Jewelry ships on 1.21.1 (EMI has no build past it).
 * <p>
 * Displays are built here, on the client, rather than server-side: the registry is synced, so the client
 * world already has everything the Jeweler's Kit itself uses to list its cuts.
 * <p>
 * Loaded reflectively by REI only: Fabric via the {@code rei_client} entrypoint in {@code fabric.mod.json},
 * NeoForge via the {@code @REIPluginClient} subclass in the neoforge module. Nothing in Jewelry references
 * this class, so it is never class-loaded when REI is absent.
 */
// No `@Environment`: Architectury turns it into `@OnlyIn`, which NeoForge 21.11+ warns about at load.
public class JewelryReiClientPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new GemCuttingCategory());
        registry.addWorkstations(GemCuttingDisplay.CATEGORY, EntryStacks.of(JewelryBlocks.JEWELERS_KIT.item()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        // An empty registry means the server has gem cuts disabled: no displays, which leaves the category hidden.
        var world = MinecraftClient.getInstance().world;
        if (world == null || !GemCutRegistry.isEnabled(world)) {
            return;
        }
        // Iterating the registry (rather than Jewelry's own gem list) picks up cuts contributed by
        // other mods and data packs, including cuts for gems Jewelry does not own.
        GemCutRegistry.from(world).streamEntries().forEach(entry -> {
            RegistryEntry<GemCut> cut = entry;
            GemCut.idOf(cut).ifPresent(cutId -> registry.add(new GemCuttingDisplay(cutId, cut)));
        });
    }
}
