package net.jewelry.gems;

import net.jewelry.JewelryMod;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.util.Identifier;

/// Data component types of the gem system.
///
/// The types are built statically and registered by [#register()] from `DataComponentTypesMixin`, at the tail
/// of vanilla's component bootstrap: earlier than any mod's item registration on both loaders, so other mods
/// can look `jewelry:sockets` up by id while building their items.
public class GemComponents {
    public static final Identifier CUT_ID = Identifier.of(JewelryMod.ID, "cut");

    /// Present on a cut gem, referencing its [GemCut]. Absent on a raw gem.
    public static final ComponentType<RegistryEntry<GemCut>> CUT = ComponentType.<RegistryEntry<GemCut>>builder()
            .codec(RegistryFixedCodec.of(GemCutRegistry.KEY))
            .packetCodec(PacketCodecs.registryEntry(GemCutRegistry.KEY))
            .build();

    public static final Identifier SOCKETS_ID = Identifier.of(JewelryMod.ID, "sockets");

    /// An item's sockets. Items declare their default count via `Item.Settings#component` with
    /// [SocketsComponent#empty]; socketing writes the filled component onto the stack.
    public static final ComponentType<SocketsComponent> SOCKETS = ComponentType.<SocketsComponent>builder()
            .codec(SocketsComponent.CODEC)
            .packetCodec(SocketsComponent.PACKET_CODEC)
            .build();

    public static final Identifier ITEM_MODEL_ID = Identifier.of(JewelryMod.ID, "item_model");

    /// Explicit per-stack item model (a plain model id such as `jewelry:item/gem_cut/bold_ruby`), the
    /// SpellEngine `item_model` component replicated. Cut gems don't need it: their model comes from the cut.
    public static final ComponentType<Identifier> ITEM_MODEL = ComponentType.<Identifier>builder()
            .codec(Identifier.CODEC)
            .packetCodec(Identifier.PACKET_CODEC)
            .build();

    public static final Identifier SOCKET_MOUNT_ID = Identifier.of(JewelryMod.ID, "socket_mount");

    /// On a socket mount item: what it fits and how many sockets it adds (see [SocketMountComponent]).
    public static final ComponentType<SocketMountComponent> SOCKET_MOUNT = ComponentType.<SocketMountComponent>builder()
            .codec(SocketMountComponent.CODEC)
            .packetCodec(SocketMountComponent.PACKET_CODEC)
            .build();

    private static boolean registered = false;

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        Registry.register(Registries.DATA_COMPONENT_TYPE, CUT_ID, CUT);
        Registry.register(Registries.DATA_COMPONENT_TYPE, SOCKET_MOUNT_ID, SOCKET_MOUNT);
        Registry.register(Registries.DATA_COMPONENT_TYPE, ITEM_MODEL_ID, ITEM_MODEL);
        Registry.register(Registries.DATA_COMPONENT_TYPE, SOCKETS_ID, SOCKETS);
    }
}
