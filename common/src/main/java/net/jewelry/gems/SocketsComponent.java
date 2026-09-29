package net.jewelry.gems;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/// The `jewelry:sockets` component: one entry per socket, in display order.
///
/// ```
/// jewelry:sockets=[ {}, {"gem": "jewelry:bold_ruby"}, {"type": "jewelry:mounted"}, {"type": "witcher:sign"} ]
/// ```
/// - `gem` (optional): the cut gem in that socket; absent = empty socket.
/// - `type` (optional): an id labelling the socket. Absent = a plain standard socket. A Socket Mount writes
///   the type its component names (`jewelry:mounted`) so its cap can be counted. A type MAY have a
///   definition in the `socket_type` registry ([SocketType]) giving it a gem filter and its own look; a type
///   without one behaves and looks like a standard socket.
///
/// Immutable: every change returns a new instance. An item's built-in sockets are its default component,
/// e.g. `[{}]` — see [#empty].
public record SocketsComponent(List<Socket> sockets) {
    /// One socket.
    public record Socket(Optional<RegistryEntry<GemCut>> gem, Optional<Identifier> type) {
        public static final Socket EMPTY = new Socket(Optional.empty(), Optional.empty());

        public static final Codec<Socket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                RegistryFixedCodec.of(GemCutRegistry.KEY).optionalFieldOf("gem").forGetter(Socket::gem),
                Identifier.CODEC.optionalFieldOf("type").forGetter(Socket::type)
        ).apply(instance, Socket::new));

        public static final PacketCodec<RegistryByteBuf, Socket> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.optional(PacketCodecs.registryEntry(GemCutRegistry.KEY)), Socket::gem,
                PacketCodecs.optional(Identifier.PACKET_CODEC), Socket::type,
                Socket::new
        );

        public static Socket ofType(Identifier type) {
            return new Socket(Optional.empty(), Optional.of(type));
        }

        public boolean isEmpty() {
            return gem.isEmpty();
        }

        public boolean isOfType(Identifier type) {
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

    /// `count` empty standard sockets — also the value to hand `Item.Settings#component` for an item's default.
    public static SocketsComponent empty(int count) {
        return new SocketsComponent(Collections.nCopies(count, Socket.EMPTY));
    }

    /// `count` empty sockets of the given type, e.g. a mod's own kind of socket as an item default.
    public static SocketsComponent empty(int count, Identifier type) {
        return new SocketsComponent(Collections.nCopies(count, Socket.ofType(type)));
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

    public int countOfType(Identifier type) {
        return (int) sockets.stream().filter(socket -> socket.isOfType(type)).count();
    }

    /// Socket a gem into the first empty socket that `accepts` it. When no accepting socket is empty, the
    /// accepting sockets — and only those — are reset: their gems are destroyed and the new gem sits alone
    /// in the first of them (the anvil offers no way to pick a socket; its result preview shows the outcome).
    /// Sockets that do not accept the gem are never touched. Null when no socket accepts the gem at all.
    @Nullable
    public SocketsComponent withGem(RegistryEntry<GemCut> gem, Predicate<Socket> accepts) {
        var updated = new ArrayList<>(sockets);
        int firstAccepting = -1;
        for (int i = 0; i < updated.size(); i++) {
            if (!accepts.test(updated.get(i))) {
                continue;
            }
            if (firstAccepting < 0) {
                firstAccepting = i;
            }
            if (updated.get(i).isEmpty()) {
                updated.set(i, updated.get(i).with(gem));
                return new SocketsComponent(updated);
            }
        }
        if (firstAccepting < 0) {
            return null;
        }
        for (int i = 0; i < updated.size(); i++) {
            if (accepts.test(updated.get(i))) {
                updated.set(i, updated.get(i).cleared());
            }
        }
        updated.set(firstAccepting, updated.get(firstAccepting).with(gem));
        return new SocketsComponent(updated);
    }

    /// `added` more empty sockets of the given type appended (see [GemSocketing#mount]).
    public SocketsComponent withAddedSockets(int added, Identifier type) {
        var updated = new ArrayList<>(sockets);
        for (int i = 0; i < added; i++) {
            updated.add(Socket.ofType(type));
        }
        return new SocketsComponent(updated);
    }
}
