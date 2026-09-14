package net.jewelry.gems;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/// Socket lines of an item tooltip, one per socket, placed right after the enchantment lines:
///
///     ◆ +4% Attack Damage      (glyph tinted with the cut's colour; the gem itself is not named)
///     ◇ Empty Socket
///
/// The glyphs are bitmap characters registered in `assets/minecraft/font/default.json`; the filled one
/// is drawn white so the text colour tints it.
public class SocketTooltip {
    /// Plane-15 private-use characters (U+F0100, U+F0101), far from the ranges other mods commonly claim.
    public static final String EMPTY_GLYPH = "\uDB80\uDD00";
    public static final String FILLED_GLYPH = "\uDB80\uDD01";

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
                    lines.add(Text.literal(FILLED_GLYPH).styled(style -> style.withColor(cut.color()))
                            .append(Text.literal(" "))
                            .append(GemItem.bonusText(cut)));
                } else {
                    lines.add(Text.literal(EMPTY_GLYPH + " ")
                            .append(Text.translatable("item.jewelry.socket.empty"))
                            .formatted(Formatting.DARK_GRAY));
                }
            }
        });
    }
}
