package net.jewelry.api.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.jewelry.gems.GemSockets;
import net.jewelry.gems.SocketType;
import net.jewelry.gems.SocketTypeRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

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
public abstract class SocketTypeTagGenerator extends FabricTagProvider<SocketType> {
    public SocketTypeTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, SocketTypeRegistry.KEY, registriesFuture);
    }

    /// Lets the grindstone take sockets of this type off an item.
    protected void grindstoneRemovable(Identifier socketType) {
        builder(GemSockets.GRINDSTONE_REMOVABLE).addOptional(RegistryKey.of(SocketTypeRegistry.KEY, socketType));
    }
}
