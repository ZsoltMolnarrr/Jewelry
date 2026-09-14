package net.jewelry.gems;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/// Socket lines of an item tooltip, one per socket, placed right after the enchantment lines:
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
    public static void appendLines(ItemStack stack, List<Text> lines, @Nullable RegistryWrapper.WrapperLookup lookup) {
        if (!GemCutRegistry.isEnabled(lookup)) {
            return;
        }
        GemSockets.of(stack).ifPresent(sockets -> {
            for (int i = 0; i < sockets.count(); i++) {
                var gem = sockets.gemAt(i);
                if (gem.isPresent()) {
                    var cut = gem.get().value();
                    lines.add(Text.literal(FRAME_GLYPH).formatted(Formatting.GRAY)
                            .append(Text.literal(BACKSPACE + GEM_GLYPH).styled(style -> style.withColor(cut.color())))
                            .append(Text.literal(" "))
                            .append(GemItem.bonusText(cut)));
                } else {
                    lines.add(Text.literal(FRAME_GLYPH + " ")
                            .append(Text.translatable("item.jewelry.socket.empty"))
                            .formatted(Formatting.DARK_GRAY));
                }
            }
        });
    }
}
