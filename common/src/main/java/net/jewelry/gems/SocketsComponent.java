package net.jewelry.gems;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/// The `jewelry:sockets` component: how many sockets an item has and which cut gems sit in them.
///
/// `gems` are the filled sockets in fill order, so `gems.size() <= count`; the remaining sockets are empty.
/// Immutable: every change returns a new instance.
public record SocketsComponent(int count, List<RegistryEntry<GemCut>> gems) {
    public static final Codec<SocketsComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, 64).fieldOf("count").forGetter(SocketsComponent::count),
            RegistryFixedCodec.of(GemCutRegistry.KEY).listOf().optionalFieldOf("gems", List.of()).forGetter(SocketsComponent::gems)
    ).apply(instance, SocketsComponent::new));

    public static final PacketCodec<RegistryByteBuf, SocketsComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, SocketsComponent::count,
            PacketCodecs.registryEntry(GemCutRegistry.KEY).collect(PacketCodecs.toList()), SocketsComponent::gems,
            SocketsComponent::new
    );

    public SocketsComponent {
        gems = List.copyOf(gems);
    }

    public static SocketsComponent empty(int count) {
        return new SocketsComponent(count, List.of());
    }

    public int filled() {
        return Math.min(gems.size(), count);
    }

    public boolean hasEmptySocket() {
        return filled() < count;
    }

    /// The gem in socket `index` (0-based), empty for an empty socket or an index past `count`.
    public Optional<RegistryEntry<GemCut>> gemAt(int index) {
        return index >= 0 && index < filled() ? Optional.of(gems.get(index)) : Optional.empty();
    }

    /// Socket a gem: fills the first empty socket, or — when every socket is full — replaces the gem in
    /// the first socket. The replaced gem is gone (World of Warcraft rules), the caller owes no refund.
    public SocketsComponent withGem(RegistryEntry<GemCut> gem) {
        var updated = new ArrayList<>(gems.subList(0, filled()));
        if (updated.size() < count) {
            updated.add(gem);
        } else if (!updated.isEmpty()) {
            updated.set(0, gem);
        }
        return new SocketsComponent(count, updated);
    }

    public SocketsComponent withCount(int count) {
        return new SocketsComponent(count, gems.subList(0, Math.min(gems.size(), count)));
    }
}
