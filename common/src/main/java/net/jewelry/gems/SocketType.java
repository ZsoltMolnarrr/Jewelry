package net.jewelry.gems;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.TextColor;
import net.minecraft.tags.TagKey;

/// Optional definition of a socket `type`, in the synced `socket_type` registry
/// (`data/<ns>/socket_type/<path>.json`, keyed by the same id a socket's `type` carries).
///
/// A socket type needs NO definition to be used: an undefined type is a standard socket with the generic look.
/// A definition is the opt-in for a distinct socket. Every field is optional:
/// - `accepts`: tag of gem cuts this socket takes — and then ONLY those. Absent → a standard socket: any cut
///   that is not in `#jewelry:restricted`.
/// - `title`: the empty-socket text. Absent → "Empty Socket".
/// - `empty` / `fill`: the [Appearance] of the socket while empty and while holding a gem.
public record SocketType(Optional<Component> title, Optional<Appearance> empty, Optional<Appearance> fill,
                         Optional<TagKey<GemCut>> accepts) {
    /// How a socket is drawn in one state; the same structure for `empty` and `fill`.
    /// - `icon`: the socket frame glyph (a character a resource pack maps to a bitmap in the font; 9×9 like
    ///   Jewelry's, so the gem drawn over a filled socket lines up). Absent → Jewelry's frame.
    /// - `color`: `empty` → colour of the whole empty-socket line; `fill` → colour of the frame (the gem is
    ///   tinted by its cut, the bonus text stays vanilla blue). Absent → dark gray / gray.
    public record Appearance(Optional<String> icon, Optional<TextColor> color) {
        public static final Appearance GENERIC = new Appearance(Optional.empty(), Optional.empty());

        public static final Codec<Appearance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("icon").forGetter(Appearance::icon),
                TextColor.CODEC.optionalFieldOf("color").forGetter(Appearance::color)
        ).apply(instance, Appearance::new));
    }

    public static final SocketType GENERIC = new SocketType(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    public static final Codec<SocketType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("title").forGetter(SocketType::title),
            Appearance.CODEC.optionalFieldOf("empty").forGetter(SocketType::empty),
            Appearance.CODEC.optionalFieldOf("fill").forGetter(SocketType::fill),
            TagKey.hashedCodec(GemCutRegistry.KEY).optionalFieldOf("accepts").forGetter(SocketType::accepts)
    ).apply(instance, SocketType::new));
}
