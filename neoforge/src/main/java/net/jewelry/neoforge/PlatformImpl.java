package net.jewelry.neoforge;

import net.jewelry.Platform;
import net.neoforged.fml.loading.FMLLoader;

public class PlatformImpl {
    public static class NeoForgeUtil implements Platform.Util {
        @Override
        public boolean isModLoaded(String modid) {
            // Exact same check as SpellEngine's Platform.Util: LoadingModList (not ModList) is populated
            // during mod discovery, before any constructor runs, so early compat gates in static
            // initializers / init match Fabric's "resolved up front" timing.
            return FMLLoader.getCurrent().getLoadingModList().getModFileById(modid) != null;
        }
    }
    private static final Platform.Util UTIL = new NeoForgeUtil();
    public static Platform.Util util() {
        return UTIL;
    }
}
