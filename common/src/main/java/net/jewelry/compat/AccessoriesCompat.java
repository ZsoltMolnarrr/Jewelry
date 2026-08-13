package net.jewelry.compat;

import net.jewelry.Platform;

public class AccessoriesCompat {
    public static void init() {
        if (Platform.util().isModLoaded("accessories")) {
            // Outsource to avoid class loading issues
            AccessoriesHelper.registerFactory();
        }
    }
}
