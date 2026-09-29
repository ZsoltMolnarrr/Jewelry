package net.jewelry.gems;

import net.jewelry.api.SocketTypeBuilder;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

/// The socket type definitions Jewelry ships, built with the public [SocketTypeBuilder] and emitted by datagen
/// as `data/jewelry/socket_type/*.json`.
public class SocketTypes {
    public static final List<SocketTypeBuilder.Entry> all = new ArrayList<>();

    private static SocketTypeBuilder.Entry add(SocketTypeBuilder builder) {
        var entry = builder.build();
        all.add(entry);
        return entry;
    }

    /// Sockets added by a Socket Mount: look only — no `accepts`, so they take every standard cut.
    /// Goldwork, like the mount: a dim gold "Empty Mounted Socket" line, a brighter gold frame once filled —
    /// the same dim/bright relation the generic dark gray / gray has.
    public static final SocketTypeBuilder.Entry MOUNTED = add(SocketTypeBuilder.create(SocketMountComponent.DEFAULT_TYPE)
            .title(Component.translatable("socket.jewelry.mounted"))
            .emptyColor(0x8F7A3A)
            .fillColor(0xC9A84C));
}
