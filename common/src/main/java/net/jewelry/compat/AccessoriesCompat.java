package net.jewelry.compat;

import net.fabricmc.loader.api.FabricLoader;

public class AccessoriesCompat {
    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("accessories")) {
            // Outsource to avoid class loading issues
            AccessoriesHelper.registerFactory();
        }
    }
}
