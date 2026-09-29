package net.jewelry.fabric;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.jewelry.Platform;

public class PlatformImpl {
    public static class FabricUtil implements Platform.Util {
        @Override
        public boolean isModLoaded(String modid) {
            return FabricLoader.getInstance().isModLoaded(modid);
        }

        @Override
        public boolean isFabric() {
            return true;
        }

        @Override
        public <T> void registerSyncedDataRegistry(RegistryKey<Registry<T>> key, Codec<T> localCodec, Codec<T> networkCodec) {
            DynamicRegistries.registerSynced(key, localCodec, networkCodec);
        }
    }
    private static final Platform.Util UTIL = new FabricUtil();
    public static Platform.Util util() {
        return UTIL;
    }
}
