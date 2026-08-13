package net.jewelry.neoforge;

import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.items.Gems;
import net.jewelry.items.Group;
import net.jewelry.items.JewelryItems;
import net.jewelry.neoforge.compat.CompatFeatures;
import net.jewelry.village.JewelryVillagers;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.poi.PointOfInterestType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(JewelryMod.ID)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        CompatFeatures.init();
        JewelryMod.init();
        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
        // Jewelry items into the Jewelry creative tab — NeoForge mod-bus event (replaces ItemGroupEvents).
        modBus.addListener(BuildCreativeModeTabContentsEvent.class, NeoForgeMod::buildTabContents);
        // Villager trades — game-bus event (fired per profession); replaces Fabric API's TradeOfferHelper.
        NeoForge.EVENT_BUS.addListener(VillagerTradesEvent.class, NeoForgeMod::onVillagerTrades);
        // Ore world-gen injection is data-driven on NeoForge — see
        // data/jewelry/neoforge/biome_modifier/gem_vein.json (replaces Fabric's BiomeModifications).
    }

    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.SOUND_EVENT, reg -> {
            JewelryMod.registerSounds();
        });
        event.register(RegistryKeys.BLOCK, reg -> {
            JewelryMod.registerBlocks();
        });
        event.register(RegistryKeys.ITEM, reg -> {
            JewelryMod.registerItems();
        });
        event.register(RegistryKeys.POINT_OF_INTEREST_TYPE, reg -> {
            // POI registration — vanilla registry insert. NeoForge's POI registry callback wires the
            // block-state -> POI mapping from the type's block states, so no Fabric API helper is needed.
            try {
                Registry.register(Registries.POINT_OF_INTEREST_TYPE, JewelryVillagers.POI_ID,
                        new PointOfInterestType(JewelryVillagers.poiBlockStates(),
                                JewelryVillagers.POI_TICKET_COUNT, JewelryVillagers.POI_SEARCH_DISTANCE));
            } catch (Exception e) {
            }
        });
        event.register(RegistryKeys.VILLAGER_PROFESSION, reg -> {
            JewelryMod.registerVillagers();
        });
    }

    private static void buildTabContents(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(Group.KEY)) {
            return;
        }
        for (var entry : Gems.all) {
            event.add(entry.item());
        }
        for (var entry : JewelryItems.all) {
            event.add(entry.item());
        }
        for (var entry : JewelryBlocks.all) {
            event.add(entry.item());
        }
    }

    private static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() != JewelryVillagers.JEWELER_PROFESSION) {
            return;
        }
        JewelryVillagers.createTrades().forEach((tier, factories) -> {
            var tierList = event.getTrades().get(tier.intValue());
            if (tierList != null) {
                tierList.addAll(factories);
            }
        });
    }
}
