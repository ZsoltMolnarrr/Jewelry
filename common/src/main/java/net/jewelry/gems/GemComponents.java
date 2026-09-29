package net.jewelry.gems;

import net.jewelry.JewelryMod;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.codec.RegistryCodecs;

/// Data component types of the gem system.
///
/// The types are built statically and registered by [#register()] from `DataComponentTypesMixin`, at the tail
/// of vanilla's component bootstrap: earlier than any mod's item registration on both loaders, so other mods
/// can look `jewelry:sockets` up by id while building their items.
public class GemComponents {
    public static final Identifier CUT_ID = Identifier.fromNamespaceAndPath(JewelryMod.ID, "cut");

    /// Present on a cut gem, referencing its [GemCut]. Absent on a raw gem.
    public static final DataComponentType<Holder<GemCut>> CUT = DataComponentType.<Holder<GemCut>>builder()
            .persistent(RegistryCodecs.holder(GemCutRegistry.KEY))
            .networkSynchronized(ByteBufCodecs.holderRegistry(GemCutRegistry.KEY))
            .build();

    public static final Identifier SOCKETS_ID = Identifier.fromNamespaceAndPath(JewelryMod.ID, "sockets");

    /// An item's sockets. Items declare their default count via `Item.Settings#component` with
    /// [SocketsComponent#empty]; socketing writes the filled component onto the stack.
    public static final DataComponentType<SocketsComponent> SOCKETS = DataComponentType.<SocketsComponent>builder()
            .persistent(SocketsComponent.CODEC)
            .networkSynchronized(SocketsComponent.PACKET_CODEC)
            .build();

    public static final Identifier ITEM_MODEL_ID = Identifier.fromNamespaceAndPath(JewelryMod.ID, "item_model");

    /// Explicit per-stack item model (a plain model id such as `jewelry:item/gem_cut/bold_ruby`), the
    /// SpellEngine `item_model` component replicated. Cut gems don't need it: their model comes from the cut.
    public static final DataComponentType<Identifier> ITEM_MODEL = DataComponentType.<Identifier>builder()
            .persistent(Identifier.CODEC)
            .networkSynchronized(Identifier.STREAM_CODEC)
            .build();

    public static final Identifier SOCKET_MOUNT_ID = Identifier.fromNamespaceAndPath(JewelryMod.ID, "socket_mount");

    /// On a socket mount item: what it fits and how many sockets it adds (see [SocketMountComponent]).
    public static final DataComponentType<SocketMountComponent> SOCKET_MOUNT = DataComponentType.<SocketMountComponent>builder()
            .persistent(SocketMountComponent.CODEC)
            .networkSynchronized(SocketMountComponent.PACKET_CODEC)
            .build();

    private static boolean registered = false;

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, CUT_ID, CUT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, SOCKET_MOUNT_ID, SOCKET_MOUNT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ITEM_MODEL_ID, ITEM_MODEL);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, SOCKETS_ID, SOCKETS);
    }
}
