package net.jewelry.mixin;

import net.jewelry.gems.GemSocketing;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Grinding an item (alone in the grindstone) that carries sockets of a `#jewelry:grindstone_removable`
/// type takes them off — gems in them included — and nothing else; enchantments survive that grind and go
/// on the next one, as vanilla does.
///
/// Same shape as Archers' Auto-Fire Hook removal (and SpellEngine's `#spell_engine:grindable`): a
/// cancellable HEAD inject guarded on "this input carries MY attachment". The three compose in any mixin
/// order: each grind strips whichever attachment's handler runs first, the next grind the next.
@Mixin(GrindstoneScreenHandler.class)
public class GrindstoneScreenHandlerMixin {
    /// The handler keeps no player; the socket type tag lives in a synced registry, reached through the
    /// player's world on either side (the client's `ScreenHandlerContext` is empty).
    @Unique
    private PlayerEntity jewelry$player;

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At("TAIL"))
    private void init_TAIL_Jewelry(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo ci) {
        this.jewelry$player = playerInventory.player;
    }

    @Inject(method = "getOutputStack", at = @At("HEAD"), cancellable = true)
    private void getOutputStack_HEAD_Jewelry(ItemStack firstInput, ItemStack secondInput, CallbackInfoReturnable<ItemStack> cir) {
        if (firstInput.isEmpty() == secondInput.isEmpty() || jewelry$player == null) {
            return; // both empty, or two items to combine
        }
        var input = firstInput.isEmpty() ? secondInput : firstInput;
        var unmounted = GemSocketing.unmount(input, jewelry$player.getWorld().getRegistryManager());
        if (unmounted != null) {
            cir.setReturnValue(unmounted);
        }
    }
}
