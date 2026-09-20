package net.jewelry.gems;

import net.jewelry.api.SocketTypeBuilder;
import net.minecraft.text.Text;

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
    /// A muted gold line (the mount is goldwork) reading "Empty Mounted Socket".
    public static final SocketTypeBuilder.Entry MOUNTED = add(SocketTypeBuilder.create(SocketMountComponent.DEFAULT_TYPE)
            .title(Text.translatable("socket.jewelry.mounted"))
            .color(0xC9A84C));
}
