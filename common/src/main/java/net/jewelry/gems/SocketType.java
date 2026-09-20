package net.jewelry.gems;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.TextColor;

import java.util.Optional;

/// Optional definition of a socket `type`, in the synced `socket_type` registry
/// (`data/<ns>/socket_type/<path>.json`, keyed by the same id a socket's `type` carries).
///
/// A socket type needs NO definition to be used: an undefined type (Jewelry's own `jewelry:mounted`, say)
/// is a standard socket with the generic look. A definition is the opt-in for a distinct socket:
/// - `accepts`: tag of gem cuts this socket takes — and then ONLY those. Absent → a standard socket: any cut
///   that is not in `#jewelry:restricted`.
/// - `icon`: the socket frame glyph (a character a resource pack maps to a bitmap in the font; 9×9 like
///   Jewelry's, so the gem overlay of a filled socket lines up). Absent → Jewelry's frame.
/// - `title`: the empty-socket text. Absent → "Empty Socket".
/// - `color`: colour of the empty-socket line, and of the frame on a filled one. Absent → gray tones.
public record SocketType(Optional<String> icon, Optional<Text> title, Optional<TextColor> color,
                         Optional<TagKey<GemCut>> accepts) {
    public static final SocketType GENERIC = new SocketType(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    public static final Codec<SocketType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("icon").forGetter(SocketType::icon),
            TextCodecs.CODEC.optionalFieldOf("title").forGetter(SocketType::title),
            TextColor.CODEC.optionalFieldOf("color").forGetter(SocketType::color),
            TagKey.codec(GemCutRegistry.KEY).optionalFieldOf("accepts").forGetter(SocketType::accepts)
    ).apply(instance, SocketType::new));
}
