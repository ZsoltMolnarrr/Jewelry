package net.jewelry.gems;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/// Putting a cut gem into an item's socket — the anvil's job (left: socketed item, right: cut gem).
public class GemSocketing {
    /// Cost in levels charged by the anvil; the item's repair-cost counter is never raised.
    public static final int LEVEL_COST = 1;

    /// The anvil's result for (left, right): a gem socketed, or a socket mount applied, or null.
    /// `ignoreMountCap`: creative players may mount past the cap.
    @Nullable
    public static ItemStack anvilResult(ItemStack left, ItemStack right, boolean ignoreMountCap) {
        var socketed = socket(left, right);
        return socketed != null ? socketed : mount(left, right, ignoreMountCap);
    }

    // MARK: Socket mounts

    /// Survival rules: the mount must fit, and the item must have room under the mount's `max_mounted` cap
    /// (one mounted socket per armor piece for the armor mount). `ignoreCap` lifts the cap, for creative.
    public static boolean canMount(ItemStack target, ItemStack mountStack, boolean ignoreCap) {
        var mount = mountStack.get(GemComponents.SOCKET_MOUNT);
        if (mount == null || !GemSockets.enabled() || !mount.fits(target)) {
            return false;
        }
        if (ignoreCap) {
            return true;
        }
        var mounted = GemSockets.of(target).map(sockets -> sockets.countOfType(mount.type())).orElse(0);
        return mounted + mount.sockets() <= mount.maxMounted();
    }

    /// The target with the mount's sockets added (empty), or null when the pair does not apply — the mount
    /// does not fit the item, or (unless `ignoreCap`) the item already carries its cap of mounted sockets.
    @Nullable
    public static ItemStack mount(ItemStack target, ItemStack mountStack, boolean ignoreCap) {
        if (!canMount(target, mountStack, ignoreCap)) {
            return null;
        }
        var mount = mountStack.get(GemComponents.SOCKET_MOUNT);
        var sockets = GemSockets.of(target).orElse(SocketsComponent.empty(0));
        var result = target.copyWithCount(1);
        result.set(GemComponents.SOCKETS, sockets.withAddedSockets(mount.sockets(), mount.type()));
        return result;
    }

    // MARK: Gems

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
