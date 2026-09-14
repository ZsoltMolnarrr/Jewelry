package net.jewelry.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.jewelry.gems.SocketTooltip;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackTooltipMixin {
    /// Socket lines go between the enchantment lines and the attribute modifiers. Anchored on the
    /// `DYED_COLOR` field read that immediately follows the enchantments append — a stable landmark on
    /// both loaders, unlike the private attribute-tooltip method NeoForge reroutes.
    @Inject(method = "getTooltip",
            at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
                    target = "Lnet/minecraft/component/DataComponentTypes;DYED_COLOR:Lnet/minecraft/component/ComponentType;"))
    private void getTooltip_sockets_Jewelry(Item.TooltipContext context, PlayerEntity player, TooltipType type,
                                            CallbackInfoReturnable<List<Text>> cir, @Local(ordinal = 0) List<Text> lines) {
        SocketTooltip.appendLines((ItemStack) (Object) this, lines, context.getRegistryLookup());
    }
}
