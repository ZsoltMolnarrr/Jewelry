package net.jewelry.gems;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;

/// Putting a cut gem into an item's socket, or a Socket Mount onto an item — the anvil's job (left: item,
/// right: gem or mount) — and taking attached sockets off again, the grindstone's job ([#unmount]).
public class GemSocketing {
    /// Cost in levels charged by the anvil; the item's repair-cost counter is never raised.
    public static final int LEVEL_COST = 1;

    /// The anvil's result for (left, right): a gem socketed, or a socket mount applied, or null.
    /// `ignoreMountCap`: creative players may mount past the cap.
    @Nullable
    public static ItemStack anvilResult(ItemStack left, ItemStack right, boolean ignoreMountCap,
                                        @Nullable RegistryWrapper.WrapperLookup registries) {
        var socketed = socket(left, right, registries);
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

    /// Whether the socket is of a type in `#jewelry:grindstone_removable`.
    public static boolean isRemovable(@Nullable RegistryWrapper.WrapperLookup registries, SocketsComponent.Socket socket) {
        return socket.type().map(type -> SocketTypeRegistry.isIn(registries, type, GemSockets.GRINDSTONE_REMOVABLE)).orElse(false);
    }

    /// Whether the grindstone still has vanilla work on the item: any enchantment it would remove (curses
    /// stay on a ground item, so they don't count).
    public static boolean hasGrindableEnchantments(ItemStack stack) {
        return EnchantmentHelper.getEnchantments(stack).getEnchantments().stream()
                .anyMatch(enchantment -> !enchantment.isIn(EnchantmentTags.CURSE));
    }

    /// The grindstone's job, once the item has no enchantments left to grind: the item with every socket of a
    /// `#jewelry:grindstone_removable` type taken off, gems inside them destroyed; every other socket (and its
    /// gem) stays. An enchanted item is disenchanted first, as vanilla does — the sockets come off on the next
    /// grind. Null when vanilla still has work, or the item has no removable socket.
    @Nullable
    public static ItemStack unmount(ItemStack target, @Nullable RegistryWrapper.WrapperLookup registries) {
        var sockets = GemSockets.of(target).orElse(null);
        if (sockets == null || hasGrindableEnchantments(target)) {
            return null;
        }
        var kept = sockets.sockets().stream().filter(socket -> !isRemovable(registries, socket)).toList();
        if (kept.size() == sockets.count()) {
            return null;
        }
        var result = target.copy();
        result.set(GemComponents.SOCKETS, new SocketsComponent(kept));
        return result;
    }

    // MARK: Gems

    public static boolean canSocket(ItemStack target, ItemStack gem) {
        return !target.isEmpty() && GemCut.of(gem).isPresent() && GemSockets.hasSockets(target);
    }

    /// The target with the gem socketed: into the first empty socket that accepts it ([GemSockets#accepts]);
    /// when none is empty, the accepting sockets alone are reset to this gem. Null when the pair does not
    /// apply, including when no socket of the item accepts this gem. The inputs are left untouched.
    @Nullable
    public static ItemStack socket(ItemStack target, ItemStack gem, @Nullable RegistryWrapper.WrapperLookup registries) {
        if (!canSocket(target, gem)) {
            return null;
        }
        var cut = GemCut.of(gem).get();
        var sockets = GemSockets.of(target).get();
        var updated = sockets.withGem(cut, socket -> GemSockets.accepts(registries, socket, cut));
        if (updated == null) {
            return null;
        }
        var result = target.copyWithCount(1);
        result.set(GemComponents.SOCKETS, updated);
        return result;
    }
}
