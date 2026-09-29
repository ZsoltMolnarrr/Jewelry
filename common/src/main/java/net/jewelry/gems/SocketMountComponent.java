package net.jewelry.gems;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/// The `jewelry:socket_mount` component: what a socket mount item does when applied to equipment at an anvil.
/// Variants (armor-only, weapon-only, …) are just items carrying different defaults of this component.
///
/// - `targets`: the items it can be applied to — an item tag (`#jewelry:socket_mountable/armor`) or a list.
/// - `sockets`: how many sockets one application adds.
/// - `type`: the socket `type` id this mount writes (default `jewelry:mounted`), see [SocketsComponent.Socket].
///   Naming a type that has a `socket_type` definition makes the mount add that kind of socket.
/// - `max_mounted`: cap on sockets of that type on a single item (its built-in sockets don't count).
public record SocketMountComponent(HolderSet<Item> targets, int sockets, Identifier type, int maxMounted) {
    public static final Identifier DEFAULT_TYPE = Identifier.fromNamespaceAndPath("jewelry", "mounted");

    public static final Codec<SocketMountComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("targets").forGetter(SocketMountComponent::targets),
            Codec.intRange(1, 64).optionalFieldOf("sockets", 1).forGetter(SocketMountComponent::sockets),
            Identifier.CODEC.optionalFieldOf("type", DEFAULT_TYPE).forGetter(SocketMountComponent::type),
            Codec.intRange(1, 64).optionalFieldOf("max_mounted", 1).forGetter(SocketMountComponent::maxMounted)
    ).apply(instance, SocketMountComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SocketMountComponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderSet(Registries.ITEM), SocketMountComponent::targets,
            ByteBufCodecs.VAR_INT, SocketMountComponent::sockets,
            Identifier.STREAM_CODEC, SocketMountComponent::type,
            ByteBufCodecs.VAR_INT, SocketMountComponent::maxMounted,
            SocketMountComponent::new
    );

    public boolean fits(ItemStack target) {
        return !target.isEmpty() && targets.contains(target.getItem().builtInRegistryHolder());
    }
}
