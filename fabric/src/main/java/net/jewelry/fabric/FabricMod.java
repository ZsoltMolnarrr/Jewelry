package net.jewelry.fabric;

import net.fabricmc.api.ModInitializer;
import net.jewelry.JewelryMod;
import net.jewelry.fabric.compat.CompatFeatures;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        CompatFeatures.init();
        JewelryMod.init();
        JewelryMod.registerSounds();
        JewelryMod.registerBlocks();
        JewelryMod.registerItems();
        JewelryMod.registerPOI();
        JewelryMod.registerVillagers();
        JewelryMod.registerWorldGen();
    }
}
