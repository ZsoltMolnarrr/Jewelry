package net.jewelry.mixin;

import net.jewelry.gems.GemSocketing;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin extends ItemCombinerMenu {
    @Shadow @Final private DataSlot cost;
    @Shadow private int repairItemCountCost;
    @Shadow private String itemName;

    private AnvilScreenHandlerMixin() {
        super(null, 0, null, null, null);
    }

    /// Socketing: left = item with sockets, right = cut gem → the item with the gem socketed (a full item
    /// is reset to the new gem alone); or left = fitting equipment, right = socket mount → the item with a
    /// socket added — repeated mounting past the cap only in creative. Both for
    /// [GemSocketing#LEVEL_COST] levels and one gem. Takes over the whole result computation for that
    /// pair (so no repair-cost bump, no "Too Expensive"); every other pair falls through to vanilla.
    /// A typed rename still applies, same rules as vanilla.
    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void updateResult_HEAD_Jewelry(CallbackInfo ci) {
        var target = this.inputSlots.getItem(0);
        var gem = this.inputSlots.getItem(1);
        var result = GemSocketing.anvilResult(target, gem, this.player.hasInfiniteMaterials(),
                this.player.level().registryAccess());
        if (result == null) {
            return;
        }
        if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
            if (!this.itemName.equals(target.getHoverName().getString())) {
                result.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
            }
        } else if (target.has(DataComponents.CUSTOM_NAME)) {
            result.remove(DataComponents.CUSTOM_NAME);
        }
        this.repairItemCountCost = 1;
        this.cost.set(GemSocketing.LEVEL_COST);
        this.resultSlots.setItem(0, result);
        this.broadcastChanges();
        ci.cancel();
    }
}
