package net.jewelry.api.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.jewelry.gems.GemCut;
import net.jewelry.gems.GemCutRegistry;
import net.jewelry.gems.GemSockets;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;

/// Tag provider for the `gem_cut` registry (`data/<ns>/tags/gem_cut/...`): group your cuts into the tag a
/// socket type `accepts`, and [#restrict] that tag so its cuts stay out of standard sockets.
///
/// ```java
/// @Override protected void configure(RegistryWrapper.WrapperLookup lookup) {
///     var signGems = builder(MY_SIGN_GEMS);
///     MyCuts.all.forEach(cut -> signGems.addOptional(RegistryKey.of(GemCutRegistry.KEY, cut.id())));
///     restrict(MY_SIGN_GEMS);
/// }
/// ```
public abstract class GemCutTagGenerator extends FabricTagProvider<GemCut> {
    public GemCutTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, GemCutRegistry.KEY, registriesFuture);
    }

    /// Adds `cuts` to `#jewelry:restricted`, the blacklist of standard sockets.
    protected void restrict(TagKey<GemCut> cuts) {
        builder(GemSockets.RESTRICTED).addOptionalTag(cuts);
    }
}
