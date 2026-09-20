package net.jewelry.api;

import net.jewelry.gems.GemCut;
import net.jewelry.gems.GemCutRegistry;
import net.jewelry.gems.SocketType;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.util.Optional;

/// Builds a socket type definition ready for datagen (`net.jewelry.api.datagen.SocketTypeGenerator`).
/// Everything is optional; a type only needs a definition when it wants its own gem filter or look.
///
/// ```java
/// SocketTypeBuilder.create(Identifier.of("witcher", "sign"))
///         .accepts(SocketTypeBuilder.cutTag(Identifier.of("witcher", "sign_gems")))
///         .icon("󰄐")
///         .title(Text.translatable("socket.witcher.sign"))
///         .color(0x7FD4C1)
///         .build();
/// ```
public class SocketTypeBuilder {
    public record Entry(Identifier id, SocketType definition) { }

    private final Identifier id;
    private Optional<String> icon = Optional.empty();
    private Optional<Text> title = Optional.empty();
    private Optional<TextColor> color = Optional.empty();
    private Optional<TagKey<GemCut>> accepts = Optional.empty();

    private SocketTypeBuilder(Identifier id) {
        this.id = id;
    }

    public static SocketTypeBuilder create(Identifier id) {
        return new SocketTypeBuilder(id);
    }

    /// A tag in the `gem_cut` registry (`data/<ns>/tags/gem_cut/<path>.json`).
    public static TagKey<GemCut> cutTag(Identifier id) {
        return TagKey.of(GemCutRegistry.KEY, id);
    }

    /// Only cuts in this tag fit the socket. Put the same tag into `#jewelry:restricted` to keep those cuts
    /// out of standard sockets (`GemCutTagGenerator.restrict` does it).
    public SocketTypeBuilder accepts(TagKey<GemCut> cuts) {
        this.accepts = Optional.of(cuts);
        return this;
    }

    /// The socket frame glyph: a character your resource pack maps to a 9×9 bitmap in `font/default.json`.
    public SocketTypeBuilder icon(String glyph) {
        this.icon = Optional.of(glyph);
        return this;
    }

    /// The empty-socket text, e.g. `Text.translatable("socket.witcher.sign")` → "Empty Sign Socket".
    public SocketTypeBuilder title(Text title) {
        this.title = Optional.of(title);
        return this;
    }

    public SocketTypeBuilder color(TextColor color) {
        this.color = Optional.of(color);
        return this;
    }

    public SocketTypeBuilder color(int rgb) {
        return color(TextColor.fromRgb(rgb));
    }

    public Entry build() {
        return new Entry(id, new SocketType(icon, title, color, accepts));
    }
}
