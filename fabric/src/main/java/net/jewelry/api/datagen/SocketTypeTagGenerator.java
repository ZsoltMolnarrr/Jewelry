package net.jewelry.api.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.jewelry.gems.GemSockets;
import net.jewelry.gems.SocketType;
import net.jewelry.gems.SocketTypeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import java.util.concurrent.CompletableFuture;

/// Tag provider for the `socket_type` registry (`data/<ns>/tags/socket_type/...`). The one tag Jewelry reads
/// is `#jewelry:grindstone_removable`: sockets of those types come off at a grindstone ([#grindstoneRemovable]).
/// Only types WITH a definition can be tagged (a tag names registry entries).
///
/// ```java
/// @Override protected void configure(RegistryWrapper.WrapperLookup lookup) {
///     grindstoneRemovable(MY_MOUNT_TYPE);
/// }
/// ```
public abstract class SocketTypeTagGenerator extends FabricTagsProvider<SocketType> {
    public SocketTypeTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, SocketTypeRegistry.KEY, registriesFuture);
    }

    /// Lets the grindstone take sockets of this type off an item.
    protected void grindstoneRemovable(Identifier socketType) {
        builder(GemSockets.GRINDSTONE_REMOVABLE).addOptional(ResourceKey.create(SocketTypeRegistry.KEY, socketType));
    }
}
