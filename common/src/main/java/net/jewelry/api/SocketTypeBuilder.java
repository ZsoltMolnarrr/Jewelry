package net.jewelry.api;

import net.jewelry.gems.GemCut;
import net.jewelry.gems.GemCutRegistry;
import net.jewelry.gems.SocketType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import java.util.Optional;

/// Builds a socket type definition ready for datagen (`net.jewelry.api.datagen.SocketTypeGenerator`).
/// Everything is optional; a type only needs a definition when it wants its own gem filter or look.
///
/// ```java
/// SocketTypeBuilder.create(Identifier.of("witcher", "sign"))
///         .accepts(SocketTypeBuilder.cutTag(Identifier.of("witcher", "sign_gems")))
///         .title(Text.translatable("socket.witcher.sign"))
///         .empty("󰄐", 0x4E8F84)
///         .fill("󰄑", 0x7FD4C1)
///         .build();
/// ```
public class SocketTypeBuilder {
    public record Entry(Identifier id, SocketType definition) { }

    private final Identifier id;
    private Optional<Component> title = Optional.empty();
    private Optional<String> emptyIcon = Optional.empty();
    private Optional<TextColor> emptyColor = Optional.empty();
    private Optional<String> fillIcon = Optional.empty();
    private Optional<TextColor> fillColor = Optional.empty();
    private Optional<TagKey<GemCut>> accepts = Optional.empty();

    private SocketTypeBuilder(Identifier id) {
        this.id = id;
    }

    public static SocketTypeBuilder create(Identifier id) {
        return new SocketTypeBuilder(id);
    }

    /// A tag in the `gem_cut` registry (`data/<ns>/tags/gem_cut/<path>.json`).
    public static TagKey<GemCut> cutTag(Identifier id) {
        return TagKey.create(GemCutRegistry.KEY, id);
    }

    /// Only cuts in this tag fit the socket. Put the same tag into `#jewelry:restricted` to keep those cuts
    /// out of standard sockets (`GemCutTagGenerator.restrict` does it).
    public SocketTypeBuilder accepts(TagKey<GemCut> cuts) {
        this.accepts = Optional.of(cuts);
        return this;
    }

    /// The empty-socket text, e.g. `Text.translatable("socket.witcher.sign")` → "Empty Sign Socket".
    public SocketTypeBuilder title(Component title) {
        this.title = Optional.of(title);
        return this;
    }

    // MARK: Appearance — `empty` and `fill` take the same two properties

    /// Frame glyph while empty: a character your resource pack maps to a 9×9 bitmap in `font/default.json`.
    public SocketTypeBuilder emptyIcon(String glyph) {
        this.emptyIcon = Optional.of(glyph);
        return this;
    }

    /// Colour of the whole empty-socket line.
    public SocketTypeBuilder emptyColor(int rgb) {
        this.emptyColor = Optional.of(TextColor.fromRgb(rgb));
        return this;
    }

    public SocketTypeBuilder empty(String glyph, int rgb) {
        return emptyIcon(glyph).emptyColor(rgb);
    }

    /// Frame glyph while holding a gem (the gem is drawn over it).
    public SocketTypeBuilder fillIcon(String glyph) {
        this.fillIcon = Optional.of(glyph);
        return this;
    }

    /// Colour of the frame while holding a gem.
    public SocketTypeBuilder fillColor(int rgb) {
        this.fillColor = Optional.of(TextColor.fromRgb(rgb));
        return this;
    }

    public SocketTypeBuilder fill(String glyph, int rgb) {
        return fillIcon(glyph).fillColor(rgb);
    }

    /// The same frame glyph in both states.
    public SocketTypeBuilder icon(String glyph) {
        return emptyIcon(glyph).fillIcon(glyph);
    }

    public Entry build() {
        return new Entry(id, new SocketType(title, appearance(emptyIcon, emptyColor), appearance(fillIcon, fillColor), accepts));
    }

    private static Optional<SocketType.Appearance> appearance(Optional<String> icon, Optional<TextColor> color) {
        return icon.isEmpty() && color.isEmpty() ? Optional.empty() : Optional.of(new SocketType.Appearance(icon, color));
    }
}
