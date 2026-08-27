package net.jewelry.neoforge;

import net.jewelry.JewelryMod;
import net.jewelry.items.Group;
import net.jewelry.neoforge.compat.CompatFeatures;
import net.jewelry.village.JewelryVillagers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(JewelryMod.ID)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        CompatFeatures.init();
        JewelryMod.init();
        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
        // Jewelry items into the Jewelry creative tab — NeoForge mod-bus event (replaces ItemGroupEvents).
        modBus.addListener(BuildCreativeModeTabContentsEvent.class, NeoForgeMod::buildTabContents);
        // Villager trades are data driven since 26.1 (`data/jewelry/{villager_trade,trade_set}/**`)
        // — `VillagerTradesEvent` no longer exists and nothing has to be registered here.
        // Ore world-gen injection is data-driven on NeoForge — see
        // data/jewelry/neoforge/biome_modifier/gem_vein.json (replaces Fabric's BiomeModifications).
    }

    public static void register(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, reg -> {
            JewelryMod.registerSounds();
        });
        event.register(Registries.BLOCK, reg -> {
            JewelryMod.registerBlocks();
        });
        event.register(Registries.ITEM, reg -> {
            JewelryMod.registerItems();
        });
        event.register(Registries.POINT_OF_INTEREST_TYPE, reg -> {
            // POI registration — vanilla registry insert. NeoForge's POI registry callback wires the
            // block-state -> POI mapping from the type's block states, so no Fabric API helper is needed.
            try {
                Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, JewelryVillagers.POI_ID,
                        new PoiType(JewelryVillagers.poiBlockStates(),
                                JewelryVillagers.POI_TICKET_COUNT, JewelryVillagers.POI_SEARCH_DISTANCE));
            } catch (Exception e) {
            }
        });
        event.register(Registries.VILLAGER_PROFESSION, reg -> {
            JewelryMod.registerVillagers();
        });
    }

    private static void buildTabContents(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(Group.KEY)) {
            return;
        }
        // Order comes from `Group.orderedEntries()` so it matches Fabric exactly.
        for (var item : Group.orderedEntries()) {
            event.accept(item);
        }
    }
}
