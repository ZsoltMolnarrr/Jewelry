package net.jewelry.gems;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/// The `jewelry:sockets` component: one entry per socket, in display order.
///
/// ```
/// jewelry:sockets=[ {}, {"gem": "jewelry:bold_ruby"}, {"type": "mounted"} ]
/// ```
/// - `gem` (optional): the cut gem in that socket; absent = empty socket.
/// - `type` (optional): what kind of socket it is. Built-in sockets have none; a Socket Mount writes
///   the type its component names (`mounted` for Jewelry's) so its cap can be counted. Free for future
///   kinds (e.g. sockets restricted to certain cuts).
///
/// Immutable: every change returns a new instance. An item's built-in sockets are its default component,
/// e.g. `[{}]` — see [#empty].
public record SocketsComponent(List<Socket> sockets) {
    /// One socket.
    public record Socket(Optional<RegistryEntry<GemCut>> gem, Optional<String> type) {
        public static final Socket EMPTY = new Socket(Optional.empty(), Optional.empty());

        public static final Codec<Socket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                RegistryFixedCodec.of(GemCutRegistry.KEY).optionalFieldOf("gem").forGetter(Socket::gem),
                Codec.STRING.optionalFieldOf("type").forGetter(Socket::type)
        ).apply(instance, Socket::new));

        public static final PacketCodec<RegistryByteBuf, Socket> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.optional(PacketCodecs.registryEntry(GemCutRegistry.KEY)), Socket::gem,
                PacketCodecs.optional(PacketCodecs.STRING), Socket::type,
                Socket::new
        );

        public static Socket ofType(String type) {
            return new Socket(Optional.empty(), Optional.of(type));
        }

        public boolean isEmpty() {
            return gem.isEmpty();
        }

        public boolean isOfType(String type) {
            return this.type.isPresent() && this.type.get().equals(type);
        }

        public Socket with(RegistryEntry<GemCut> gem) {
            return new Socket(Optional.of(gem), type);
        }

        public Socket cleared() {
            return new Socket(Optional.empty(), type);
        }
    }

    public static final Codec<SocketsComponent> CODEC = Socket.CODEC.listOf()
            .xmap(SocketsComponent::new, SocketsComponent::sockets);

    public static final PacketCodec<RegistryByteBuf, SocketsComponent> PACKET_CODEC =
            Socket.PACKET_CODEC.collect(PacketCodecs.toList()).xmap(SocketsComponent::new, SocketsComponent::sockets);

    public SocketsComponent {
        sockets = List.copyOf(sockets);
    }

    /// `count` empty built-in sockets — also the value to hand `Item.Settings#component` for an item's default.
    public static SocketsComponent empty(int count) {
        return new SocketsComponent(Collections.nCopies(count, Socket.EMPTY));
    }

    public int count() {
        return sockets.size();
    }

    public int filled() {
        return (int) sockets.stream().filter(socket -> !socket.isEmpty()).count();
    }

    public boolean hasEmptySocket() {
        return sockets.stream().anyMatch(Socket::isEmpty);
    }

    /// The gem in socket `index` (0-based), empty for an empty socket or an index past `count`.
    public Optional<RegistryEntry<GemCut>> gemAt(int index) {
        return index >= 0 && index < sockets.size() ? sockets.get(index).gem() : Optional.empty();
    }

    public int countOfType(String type) {
        return (int) sockets.stream().filter(socket -> socket.isOfType(type)).count();
    }

    /// Socket a gem: fills the first empty socket. When every socket is already full, the item is reset —
    /// all previous gems are destroyed and the new gem alone sits in socket one — since the anvil offers
    /// no way to pick a socket. The anvil's result preview shows this before the player commits.
    public SocketsComponent withGem(RegistryEntry<GemCut> gem) {
        if (sockets.isEmpty()) {
            return this;
        }
        var updated = new ArrayList<>(sockets);
        for (int i = 0; i < updated.size(); i++) {
            if (updated.get(i).isEmpty()) {
                updated.set(i, updated.get(i).with(gem));
                return new SocketsComponent(updated);
            }
        }
        for (int i = 0; i < updated.size(); i++) {
            updated.set(i, updated.get(i).cleared());
        }
        updated.set(0, updated.get(0).with(gem));
        return new SocketsComponent(updated);
    }

    /// `added` more empty sockets of the given type appended (see [GemSocketing#mount]).
    public SocketsComponent withAddedSockets(int added, String type) {
        var updated = new ArrayList<>(sockets);
        for (int i = 0; i < added; i++) {
            updated.add(Socket.ofType(type));
        }
        return new SocketsComponent(updated);
    }
}
