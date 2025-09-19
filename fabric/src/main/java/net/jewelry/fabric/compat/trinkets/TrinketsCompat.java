package net.jewelry.fabric.compat.trinkets;

import net.fabricmc.loader.api.FabricLoader;

public class TrinketsCompat {
    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            // Outsource to avoid class loading issues
            TrinketsHelper.registerFactory();
        }
    }
}
