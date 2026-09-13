package net.jewelry;

import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

/// Loader-neutral platform seam, mirroring SpellEngine's `net.spell_engine.Platform`. Jewelry depends on
/// SpellEngine only at COMPILE time (`modCompileOnly`) and never at runtime, so it cannot borrow
/// SpellEngine's own `Platform` — it carries this equivalent seam instead. The `@ExpectPlatform` method
/// is rewired by the Architectury transformer to the matching `PlatformImpl` in each loader's module,
/// so `common` needs no loader API and the check is resolved statically (no mutable global to install).
public class Platform {
    public interface Util {
        /// Whether another mod is present. Fabric: `FabricLoader.isModLoaded`; NeoForge: the exact same
        /// `LoadingModList.get().getModFileById(modid) != null` SpellEngine uses — populated during mod
        /// discovery (before any constructor runs), so early compat gates read a correct answer.
        boolean isModLoaded(String modid);

        /// Which loader is running; the item-model lookup differs (see `ItemRendererMixin`).
        boolean isFabric();

        /// Register a synced datapack registry (mirrors SpellEngine's seam). Fabric: `DynamicRegistries.registerSynced`;
        /// NeoForge: buffered and flushed into `DataPackRegistryEvent.NewRegistry`, the only point NeoForge accepts one.
        <T> void registerSyncedDataRegistry(RegistryKey<Registry<T>> key, Codec<T> localCodec, Codec<T> networkCodec);
    }

    @ExpectPlatform
    public static Util util() {
        throw new AssertionError();
    }
}
