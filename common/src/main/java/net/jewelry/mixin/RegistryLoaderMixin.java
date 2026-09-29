package net.jewelry.mixin;

import com.mojang.serialization.Decoder;
import net.jewelry.gems.GemCutRegistry;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryOps;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RegistryLoader.class)
public class RegistryLoaderMixin {
    /// The server-side kill switch for gem cuts: with `features.gem_cuts` off, the `gem_cut` registry is
    /// simply never loaded from resources, so it stays empty — for Jewelry's own files and any pack's alike —
    /// and is synced to clients empty. Only the server ever runs this path (clients receive synced
    /// registries over the network), so no client-side flag is needed.
    // Full descriptor: `loadFromResource` is overloaded (the public one takes a DynamicRegistryManager + List).
    @Inject(method = "loadFromResource(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/RegistryOps$RegistryInfoGetter;Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V",
            at = @At("HEAD"), cancellable = true)
    private static <E> void loadFromResource_HEAD_Jewelry(ResourceManager resourceManager,
                                                          RegistryOps.RegistryInfoGetter infoGetter,
                                                          MutableRegistry<E> registry,
                                                          Decoder<E> elementDecoder,
                                                          Map<RegistryKey<?>, Exception> errors,
                                                          CallbackInfo ci) {
        if (registry.getKey().equals(GemCutRegistry.KEY) && !GemCutRegistry.loadingEnabled()) {
            ci.cancel();
        }
    }
}
