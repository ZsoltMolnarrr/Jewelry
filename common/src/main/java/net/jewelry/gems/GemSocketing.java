package net.jewelry.gems;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/// Putting a cut gem into an item's socket — the anvil's job (left: socketed item, right: cut gem).
public class GemSocketing {
    /// Cost in levels charged by the anvil; the item's repair-cost counter is never raised.
    public static final int LEVEL_COST = 1;

    public static boolean canSocket(ItemStack target, ItemStack gem) {
        return !target.isEmpty() && GemCut.of(gem).isPresent() && GemSockets.hasSockets(target);
    }

    /// The target with the gem socketed (first empty socket; a fully gemmed item is reset to this gem
    /// alone, the old gems destroyed), or null when the pair does not apply. The inputs are left untouched.
    @Nullable
    public static ItemStack socket(ItemStack target, ItemStack gem) {
        if (!canSocket(target, gem)) {
            return null;
        }
        var cut = GemCut.of(gem).get();
        var sockets = GemSockets.of(target).get();
        var result = target.copyWithCount(1);
        result.set(GemComponents.SOCKETS, sockets.withGem(cut));
        return result;
    }
}
