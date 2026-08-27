package net.jewelry.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper;
import net.jewelry.JewelryMod;
import net.jewelry.fabric.compat.CompatFeatures;
import net.jewelry.fabric.worldgen.OreGeneration;
import net.jewelry.items.Group;
import net.jewelry.village.JewelryVillagers;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        CompatFeatures.init();
        JewelryMod.init();
        JewelryMod.registerSounds();
        JewelryMod.registerBlocks();
        JewelryMod.registerItems();

        // Villager POI — Fabric API registration (loader-specific; NeoForge does its own).
        PoiHelper.register(JewelryVillagers.POI_ID,
                JewelryVillagers.POI_TICKET_COUNT, JewelryVillagers.POI_SEARCH_DISTANCE,
                JewelryVillagers.poiBlockStates());
        JewelryMod.registerVillagers(); // registers the jeweler profession
        // Trades themselves are data driven since 26.1 (`data/jewelry/{villager_trade,trade_set}/**`)
        // — `TradeOfferHelper` is gone.

        // Ore world-gen injection — Fabric API BiomeModifications (NeoForge uses a biome_modifier JSON).
        OreGeneration.register();

        // Creative-tab placement (Jewelry group) — Fabric API.
        // Order comes from `Group.orderedEntries()` so it matches NeoForge exactly.
        CreativeModeTabEvents.modifyOutputEvent(Group.KEY).register(content -> {
            for (var item : Group.orderedEntries()) {
                content.accept(item);
            }
        });
    }
}
