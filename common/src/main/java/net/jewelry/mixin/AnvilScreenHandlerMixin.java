package net.jewelry.mixin;

import net.jewelry.gems.GemSocketing;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow @Final private Property levelCost;
    @Shadow private int repairItemUsage;
    @Shadow private String newItemName;

    private AnvilScreenHandlerMixin() {
        super(null, 0, null, null);
    }

    /// Socketing: left = item with sockets, right = cut gem → the item with the gem socketed (a full item
    /// is reset to the new gem alone); or left = fitting equipment, right = socket mount → the item with a
    /// socket added — repeated mounting past the cap only in creative. Both for
    /// [GemSocketing#LEVEL_COST] levels and one gem. Takes over the whole result computation for that
    /// pair (so no repair-cost bump, no "Too Expensive"); every other pair falls through to vanilla.
    /// A typed rename still applies, same rules as vanilla.
    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void updateResult_HEAD_Jewelry(CallbackInfo ci) {
        var target = this.input.getStack(0);
        var gem = this.input.getStack(1);
        var result = GemSocketing.anvilResult(target, gem, this.player.isInCreativeMode());
        if (result == null) {
            return;
        }
        if (this.newItemName != null && !StringHelper.isBlank(this.newItemName)) {
            if (!this.newItemName.equals(target.getName().getString())) {
                result.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
            }
        } else if (target.contains(DataComponentTypes.CUSTOM_NAME)) {
            result.remove(DataComponentTypes.CUSTOM_NAME);
        }
        this.repairItemUsage = 1;
        this.levelCost.set(GemSocketing.LEVEL_COST);
        this.output.setStack(0, result);
        this.sendContentUpdates();
        ci.cancel();
    }
}
