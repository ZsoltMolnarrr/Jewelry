package net.jewelry.mixin;

import net.jewelry.gems.GemSocketing;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Grinding an item (alone in the grindstone) that has no enchantments left to grind but carries sockets
/// of a `#jewelry:grindstone_removable` type takes them off — gems in them included. An enchanted item is
/// disenchanted first (vanilla, untouched); the sockets come off on the next grind.
///
/// Same shape as Archers' Auto-Fire Hook removal (and SpellEngine's `#spell_engine:grindable`): a
/// cancellable HEAD inject guarded on "this input carries MY attachment". They compose in any mixin
/// order: each grind strips whichever attachment's handler runs first, the next grind the next.
@Mixin(GrindstoneMenu.class)
public class GrindstoneScreenHandlerMixin {
    /// The handler keeps no player; the socket type tag lives in a synced registry, reached through the
    /// player's world on either side (the client's `ScreenHandlerContext` is empty).
    @Unique
    private Player jewelry$player;

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
    private void init_TAIL_Jewelry(int syncId, Inventory playerInventory, ContainerLevelAccess context, CallbackInfo ci) {
        this.jewelry$player = playerInventory.player;
    }

    @Inject(method = "computeResult", at = @At("HEAD"), cancellable = true)
    private void getOutputStack_HEAD_Jewelry(ItemStack firstInput, ItemStack secondInput, CallbackInfoReturnable<ItemStack> cir) {
        if (firstInput.isEmpty() == secondInput.isEmpty() || jewelry$player == null) {
            return; // both empty, or two items to combine
        }
        var input = firstInput.isEmpty() ? secondInput : firstInput;
        var unmounted = GemSocketing.unmount(input, jewelry$player.level().registryAccess());
        if (unmounted != null) {
            cir.setReturnValue(unmounted);
        }
    }
}
