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

/// The `jewelry:socket_mount` component: what a socket mount item does when applied to equipment at an anvil.
/// Variants (armor-only, weapon-only, …) are just items carrying different defaults of this component.
///
/// - `targets`: the items it can be applied to — an item tag (`#jewelry:socket_mountable/armor`) or a list.
/// - `sockets`: how many sockets one application adds.
/// - `type`: the socket `type` this mount writes (default `mounted`), see [SocketsComponent.Socket].
/// - `max_mounted`: cap on sockets of that type on a single item (its built-in sockets don't count).
public record SocketMountComponent(RegistryEntryList<Item> targets, int sockets, String type, int maxMounted) {
    public static final String DEFAULT_TYPE = "mounted";

    public static final Codec<SocketMountComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.entryList(RegistryKeys.ITEM).fieldOf("targets").forGetter(SocketMountComponent::targets),
            Codec.intRange(1, 64).optionalFieldOf("sockets", 1).forGetter(SocketMountComponent::sockets),
            Codec.STRING.optionalFieldOf("type", DEFAULT_TYPE).forGetter(SocketMountComponent::type),
            Codec.intRange(1, 64).optionalFieldOf("max_mounted", 1).forGetter(SocketMountComponent::maxMounted)
    ).apply(instance, SocketMountComponent::new));

    public static final PacketCodec<RegistryByteBuf, SocketMountComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.registryEntryList(RegistryKeys.ITEM), SocketMountComponent::targets,
            PacketCodecs.VAR_INT, SocketMountComponent::sockets,
            PacketCodecs.STRING, SocketMountComponent::type,
            PacketCodecs.VAR_INT, SocketMountComponent::maxMounted,
            SocketMountComponent::new
    );

    public boolean fits(ItemStack target) {
        return !target.isEmpty() && targets.contains(target.getItem().getRegistryEntry());
    }
}
