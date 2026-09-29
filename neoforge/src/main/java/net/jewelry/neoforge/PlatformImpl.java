package net.jewelry.neoforge;

import com.mojang.serialization.Codec;
import net.jewelry.Platform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
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

        @Override
        public boolean isFabric() {
            return false;
        }

        @Override
        public <T> void registerSyncedDataRegistry(ResourceKey<Registry<T>> key, Codec<T> localCodec, Codec<T> networkCodec) {
            // Buffered until DataPackRegistryEvent.NewRegistry — NeoForge can't register these imperatively.
            SyncedDataRegistrar.buffer(key, localCodec, networkCodec);
        }
    }
    private static final Platform.Util UTIL = new NeoForgeUtil();
    public static Platform.Util util() {
        return UTIL;
    }
}
