package net.jewelry.neoforge.compat.curios;

import net.jewelry.Platform;

public class CuriosCompat {
    public static void init() {
        if (Platform.util().isModLoaded("curios")) {
            // Outsource to avoid class loading issues
            CuriosHelper.registerFactory();
        }
    }
}
