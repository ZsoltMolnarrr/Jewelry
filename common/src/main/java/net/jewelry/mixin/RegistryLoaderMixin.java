package net.jewelry.mixin;

import net.jewelry.gems.GemCutRegistry;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceManagerRegistryLoadTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

@Mixin(ResourceManagerRegistryLoadTask.class)
public abstract class RegistryLoaderMixin<T> extends RegistryLoadTask<T> {
    private RegistryLoaderMixin() {
        super(null, null, null);
    }

    /// The server-side kill switch for gem cuts: with `features.gem_cuts` off, the `gem_cut` registry is
    /// simply never loaded from resources, so it stays empty — for Jewelry's own files and any pack's alike —
    /// and is synced to clients empty. Only the server ever runs this path (clients receive synced
    /// registries over the network, through `NetworkRegistryLoadTask`), so no client-side flag is needed.
    ///
    /// 26.1: registries load in parallel, one load task each (`RegistryDataLoader.loadContentsFromManager`
    /// is gone). The disabled registry completes at once with no elements and no tags — the two steps a
    /// task must have done before it can be frozen.
    @Inject(method = "load", at = @At("HEAD"), cancellable = true)
    private void load_HEAD_Jewelry(RegistryOps.RegistryInfoLookup context, Executor executor, CallbackInfoReturnable<CompletableFuture<?>> cir) {
        if (this.registryKey().equals(GemCutRegistry.KEY) && !GemCutRegistry.loadingEnabled()) {
            this.registerElements(Stream.empty());
            this.registerTags(Map.of());
            cir.setReturnValue(CompletableFuture.completedFuture(null));
        }
    }
}
