package net.jewelry.neoforge;

import net.jewelry.JewelryMod;
import net.jewelry.neoforge.compat.CompatFeatures;
import net.minecraft.registry.RegistryKeys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(JewelryMod.ID)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        CompatFeatures.init();
        JewelryMod.init();
        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
        modBus.addListener(NeoForgeMod::registerSpawnPlacements);
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
            try {
                JewelryMod.registerPOI();
            } catch (Exception e) {
            }
        });
        event.register(RegistryKeys.VILLAGER_PROFESSION, reg -> {
            JewelryMod.registerVillagers();
        });
    }

    private static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        JewelryMod.registerWorldGen();
    }
}