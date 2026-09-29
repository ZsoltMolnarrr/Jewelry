package net.jewelry.gems;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenTexts;
import org.jetbrains.annotations.Nullable;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

/// Socket lines of an item tooltip, one per socket, as their own paragraph right after the enchantment lines:
///
///     ◆ +4% Attack Damage      (gray socket frame with the gem inside tinted in the cut's colour)
///     ◇ Empty Socket           (the frame alone)
///
/// Duo-tone icon from single-colour font glyphs: the FRAME glyph styled gray, then a BACKSPACE (a `space`
/// provider glyph with a negative advance) that walks the cursor back over it, then the GEM glyph styled
/// with the cut's colour, drawn in the same spot. All registered in `assets/minecraft/font/default.json`;
/// the bitmaps are white so the text colour tints them.
public class SocketTooltip {
    /// Plane-15 private-use characters (U+F0100..F0102), far from the ranges other mods commonly claim.
    public static final String FRAME_GLYPH = "\uDB80\uDD00";
    public static final String GEM_GLYPH = "\uDB80\uDD01";
    public static final String BACKSPACE = "\uDB80\uDD02";

    /// `lookup` is the tooltip context's registry lookup: no socket lines at all while the server has gem cuts
    /// disabled (empty synced registry), so a disabled server shows no trace of the system.
    public static void appendLines(ItemStack stack, Consumer<Text> lines, @Nullable RegistryWrapper.WrapperLookup lookup) {
        if (!GemCutRegistry.isEnabled(lookup)) {
            return;
        }
        GemSockets.of(stack).ifPresent(sockets -> {
            if (sockets.count() == 0) {
                return;
            }
            // Own paragraph, like vanilla's attribute sections: a blank line separates it from the enchantments
            lines.accept(ScreenTexts.EMPTY);
            for (var socket : sockets.sockets()) {
                // A socket type may bring its own title and an appearance (frame glyph + colour) per state;
                // anything it leaves out — and every type without a definition — uses the generic look.
                var type = socket.type().flatMap(id -> SocketTypeRegistry.find(lookup, id)).orElse(SocketType.GENERIC);
                if (socket.gem().isPresent()) {
                    var cut = socket.gem().get().value();
                    var look = type.fill().orElse(SocketType.Appearance.GENERIC);
                    var frame = look.icon().orElse(FRAME_GLYPH);
                    var frameColor = look.color().orElse(TextColor.fromFormatting(Formatting.GRAY));
                    lines.accept(Text.literal(frame).styled(style -> style.withColor(frameColor))
                            .append(Text.literal(BACKSPACE + GEM_GLYPH).styled(style -> style.withColor(cut.color())))
                            .append(Text.literal(" "))
                            .append(cut.bonus().description()));
                } else {
                    var look = type.empty().orElse(SocketType.Appearance.GENERIC);
                    var frame = look.icon().orElse(FRAME_GLYPH);
                    var lineColor = look.color().orElse(TextColor.fromFormatting(Formatting.DARK_GRAY));
                    var title = type.title().map(Text::copy).orElseGet(() -> Text.translatable("item.jewelry.socket.empty"));
                    lines.accept(Text.literal(frame + " ").append(title).styled(style -> style.withColor(lineColor)));
                }
            }
        });
    }
}
