package net.jewelry.forge;

import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.forge.client.ForgeClientMod;
import net.jewelry.forge.compat.CompatFeatures;
import net.jewelry.items.Gems;
import net.jewelry.items.Group;
import net.jewelry.items.JewelryItems;
import net.jewelry.village.JewelryVillagers;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;

@Mod(JewelryMod.ID)
public final class ForgeMod {
    @SuppressWarnings("removal")
    public ForgeMod() {
        CompatFeatures.init();
        JewelryMod.init();
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(EventPriority.NORMAL, false, RegisterEvent.class, ForgeMod::register);
        // Jewelry items into the Jewelry creative tab — Forge mod-bus event (replaces ItemGroupEvents).
        modBus.addListener(EventPriority.NORMAL, false, BuildCreativeModeTabContentsEvent.class, ForgeMod::buildTabContents);
        // Villager trades — game-bus event (fired per profession); replaces Fabric API's TradeOfferHelper.
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, VillagerTradesEvent.class, ForgeMod::onVillagerTrades);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ForgeClientMod.register(modBus);
        }
        // Ore world-gen injection is data-driven on Forge — see
        // data/jewelry/forge/biome_modifier/gem_vein.json (replaces Fabric's BiomeModifications).
    }

    /// Forge 47 unfreezes exactly one registry per `RegisterEvent` window, so every registry gets its own.
    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.SOUND_EVENT, reg -> {
            JewelryMod.registerSounds();
        });
        event.register(RegistryKeys.BLOCK, reg -> {
            JewelryMod.registerBlocks();
        });
        event.register(RegistryKeys.ITEM, reg -> {
            // The item group is a vanilla-only registry that stays unfrozen for the whole RegisterEvent
            // phase, so registering it from the ITEM window is fine. The BlockItems come along here too.
            JewelryMod.registerItems();
        });
        event.register(RegistryKeys.POINT_OF_INTEREST_TYPE, reg -> {
            // POI registration — vanilla registry insert. Forge 47's PointOfInterestTypeCallbacks wires
            // the block-state -> POI mapping from the type's block states, so no helper is needed.
            Registry.register(Registries.POINT_OF_INTEREST_TYPE, JewelryVillagers.POI_ID,
                    new PointOfInterestType(JewelryVillagers.poiBlockStates(),
                            JewelryVillagers.POI_TICKET_COUNT, JewelryVillagers.POI_SEARCH_DISTANCE));
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
