package net.jewelry.mixin.client;

import net.jewelry.gems.SocketTooltip;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackTooltipMixin {
    /// Socket lines go between the enchantment lines and the attribute modifiers, right before the
    /// `DYED_COLOR` line that immediately follows the enchantments.
    /// 26.1+: NeoForge no longer runs the vanilla tooltip body; its default appenders call
    /// `addToTooltip(type, ...)` per component instead. Both loaders emit `DYED_COLOR` through this method,
    /// so hooking it is the common landmark.
    @Inject(method = "addToTooltip(Lnet/minecraft/core/component/DataComponentType;Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V",
            at = @At("HEAD"))
    private void addToTooltip_sockets_Jewelry(DataComponentType<?> type, Item.TooltipContext context, TooltipDisplay display,
                                              Consumer<Component> textConsumer, TooltipFlag flag, CallbackInfo ci) {
        if (type == DataComponents.DYED_COLOR) {
            SocketTooltip.appendLines((ItemStack) (Object) this, textConsumer, context.registries());
        }
    }
}
