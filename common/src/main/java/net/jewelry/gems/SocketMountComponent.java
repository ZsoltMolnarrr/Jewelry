package net.jewelry.gems;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;

/// The `jewelry:socket_mount` component: what a socket mount item does when applied to equipment at an anvil.
/// Variants (armor-only, weapon-only, …) are just items carrying different defaults of this component.
///
/// - `targets`: the items it can be applied to — an item tag (`#jewelry:socket_mountable/armor`) or a list.
/// - `sockets`: how many sockets one application adds.
/// - `type`: the socket `type` id this mount writes (default `jewelry:mounted`), see [SocketsComponent.Socket].
///   Naming a type that has a `socket_type` definition makes the mount add that kind of socket.
/// - `max_mounted`: cap on sockets of that type on a single item (its built-in sockets don't count).
public record SocketMountComponent(RegistryEntryList<Item> targets, int sockets, Identifier type, int maxMounted) {
    public static final Identifier DEFAULT_TYPE = Identifier.of("jewelry", "mounted");

    public static final Codec<SocketMountComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.entryList(RegistryKeys.ITEM).fieldOf("targets").forGetter(SocketMountComponent::targets),
            Codec.intRange(1, 64).optionalFieldOf("sockets", 1).forGetter(SocketMountComponent::sockets),
            Identifier.CODEC.optionalFieldOf("type", DEFAULT_TYPE).forGetter(SocketMountComponent::type),
            Codec.intRange(1, 64).optionalFieldOf("max_mounted", 1).forGetter(SocketMountComponent::maxMounted)
    ).apply(instance, SocketMountComponent::new));

    public static final PacketCodec<RegistryByteBuf, SocketMountComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.registryEntryList(RegistryKeys.ITEM), SocketMountComponent::targets,
            PacketCodecs.VAR_INT, SocketMountComponent::sockets,
            Identifier.PACKET_CODEC, SocketMountComponent::type,
            PacketCodecs.VAR_INT, SocketMountComponent::maxMounted,
            SocketMountComponent::new
    );

    public boolean fits(ItemStack target) {
        return !target.isEmpty() && targets.contains(target.getItem().getRegistryEntry());
    }
}
