package net.jewelry.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.fabric.compat.CompatFeatures;
import net.jewelry.fabric.worldgen.OreGeneration;
import net.jewelry.items.Gems;
import net.jewelry.items.Group;
import net.jewelry.items.JewelryItems;
import net.jewelry.village.JewelryVillagers;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        CompatFeatures.init();
        JewelryMod.init();
        JewelryMod.registerSounds();
        JewelryMod.registerBlocks();
        JewelryMod.registerItems();

        // Villager POI + trades — Fabric API registration (loader-specific; NeoForge does its own).
        PointOfInterestHelper.register(JewelryVillagers.POI_ID,
                JewelryVillagers.POI_TICKET_COUNT, JewelryVillagers.POI_SEARCH_DISTANCE,
                JewelryVillagers.poiBlockStates());
        JewelryMod.registerVillagers(); // registers the jeweler profession
        JewelryVillagers.createTrades().forEach((tier, factories) ->
                TradeOfferHelper.registerVillagerOffers(JewelryVillagers.JEWELER_PROFESSION_KEY, tier,
                        list -> list.addAll(factories)));

        // Ore world-gen injection — Fabric API BiomeModifications (NeoForge uses a biome_modifier JSON).
        OreGeneration.register();

        // Creative-tab placement (Jewelry group) — Fabric API.
        ItemGroupEvents.modifyEntriesEvent(Group.KEY).register(content -> {
            for (var entry : Gems.all) {
                content.add(entry.item());
            }
            for (var entry : JewelryItems.all) {
                content.add(entry.item());
            }
            for (var entry : JewelryBlocks.all) {
                content.add(entry.item());
            }
        });
    }
}
