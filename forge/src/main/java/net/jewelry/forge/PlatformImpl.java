package net.jewelry.forge;

import net.jewelry.Platform;
import net.minecraftforge.fml.loading.LoadingModList;

public class PlatformImpl {
    public static class ForgeUtil implements Platform.Util {
        @Override
        public boolean isModLoaded(String modid) {
            // Exact same check as SpellEngine's Platform.Util: LoadingModList (not ModList) is populated
            // during mod discovery, before any constructor runs, so early compat gates in static
            // initializers / init match Fabric's "resolved up front" timing.
            return LoadingModList.get().getModFileById(modid) != null;
        }
    }
    private static final Platform.Util UTIL = new ForgeUtil();
    public static Platform.Util util() {
        return UTIL;
    }
}
