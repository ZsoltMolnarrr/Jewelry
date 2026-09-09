package net.jewelry.forge;

import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.forge.client.ForgeClientMod;
import net.jewelry.forge.compat.CompatFeatures;
import net.jewelry.items.Gems;
import net.jewelry.items.Group;
import net.jewelry.items.JewelryItems;
import net.jewelry.util.SoundHelper;
import net.jewelry.village.JewelryVillagers;
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

    /// Registration is duplicated here rather than delegated to `common`'s registerX() methods, because a
    /// plain `Registry.register` is not usable on this loader: Forge only clears the vanilla registry's own
    /// lock from 47.4.0 onwards, so on 47.0-47.3 and NeoForge 1.20.1 it throws "Can not register to a locked
    /// registry" even inside the correct `RegisterEvent` window. The helper this event hands out is the API
    /// every build of [47,) sanctions, so Forge iterates the same content `common` exposes and registers it
    /// itself. `common` keeps its own vanilla-shaped registration for Fabric.
    ///
    /// `event.register` is a no-op unless its key matches the event's registry, so all six blocks are
    /// declared unconditionally; Forge posts one event per registry and each block runs in exactly its own.
    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.SOUND_EVENT, helper -> {
            helper.register(SoundHelper.JEWELRY_EQUIP_ID, SoundHelper.JEWELRY_EQUIP);
            helper.register(SoundHelper.JEWELRY_WORKBENCH_ID, SoundHelper.JEWELRY_WORKBENCH);
            // `SoundHelper.JEWELRY_EQUIP_ENTRY` is deliberately not reproduced: the helper returns void and
            // nothing ever reads that field — both compat layers play the raw sound event.
        });

        event.register(RegistryKeys.BLOCK, helper -> {
            for (var entry : JewelryBlocks.all) {
                helper.register(entry.id(), entry.block());
            }
        });

        event.register(RegistryKeys.ITEM, helper -> {
            // Same order as `JewelryMod.registerItems()`: block items, gems, then the jewelry pieces.
            for (var entry : JewelryBlocks.all) {
                helper.register(entry.id(), entry.item());
            }
            for (var entry : Gems.all) {
                helper.register(entry.id(), entry.item());
            }
            // The jewelry items are built here rather than earlier: `Item`'s constructor takes an intrusive
            // registry holder, so they can only be created inside this window. Their attribute lookups
            // resolve because Forge posts the `attribute` event before the `item` one.
            JewelryMod.createJewelryItems();
            for (var entry : JewelryItems.all) {
                helper.register(entry.id(), entry.item());
            }
        });

        // The item group gets its own block: `creative_mode_tab` is second to last of the ~66 registry
        // events, so registering it from the ITEM window above would be a silent key mismatch and the tab
        // would simply never exist. `creative_mode_tab` is a vanilla-only registry, so the helper falls
        // through to a plain `Registry.register` — which is fine, only Forge-wrapped registries are locked.
        event.register(RegistryKeys.ITEM_GROUP, helper -> helper.register(Group.KEY, Group.JEWELRY));

        event.register(RegistryKeys.POINT_OF_INTEREST_TYPE, helper -> {
            // Forge 47's PointOfInterestTypeCallbacks wires the block-state -> POI mapping from the type's
            // block states as the entry is added, so nothing else is needed here.
            helper.register(JewelryVillagers.POI_ID,
                    new PointOfInterestType(JewelryVillagers.poiBlockStates(),
                            JewelryVillagers.POI_TICKET_COUNT, JewelryVillagers.POI_SEARCH_DISTANCE));
        });

        event.register(RegistryKeys.VILLAGER_PROFESSION, helper -> {
            helper.register(JewelryVillagers.JEWELER_ID, JewelryVillagers.jewelerProfessionToRegister());
            // The helper returns void, so the field `VillagerTradesEvent` filters on is filled in afterwards.
            JewelryVillagers.linkProfessionEntry();
        });
    }

    private static void buildTabContents(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(Group.KEY)) {
            return;
        }
        // Blocks first, then gems, then the jewelry items — keep this order in sync with the Fabric
        // entrypoint's `ItemGroupEvents` listener, so both loaders show the same tab.
        for (var entry : JewelryBlocks.all) {
            event.add(entry.item());
        }
        for (var entry : Gems.all) {
            event.add(entry.item());
        }
        for (var entry : JewelryItems.all) {
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
