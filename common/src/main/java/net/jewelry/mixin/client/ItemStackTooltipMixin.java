package net.jewelry.mixin.client;

import net.jewelry.gems.SocketTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackTooltipMixin {
    /// Socket lines go between the enchantment lines and the attribute modifiers. Anchored on the
    /// `DYED_COLOR` field read that immediately follows the enchantments append — a stable landmark on
    /// both loaders, unlike the private attribute-tooltip method NeoForge reroutes.
    /// 1.21.5+: the tooltip body moved from `getTooltip` into `appendTooltip`, which writes to a text sink.
    /// 26.1: NeoForge splits the vanilla body into `addDetailsToTooltipComponents` (same parameters), which
    /// is where the landmark lives there; vanilla and Fabric keep it in `addDetailsToTooltip`. Exactly one
    /// of the two matches on either loader.
    @Inject(method = {"addDetailsToTooltip", "addDetailsToTooltipComponents"}, require = 1, allow = 1,
            at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
                    target = "Lnet/minecraft/core/component/DataComponents;DYED_COLOR:Lnet/minecraft/core/component/DataComponentType;"))
    private void appendTooltip_sockets_Jewelry(Item.TooltipContext context, TooltipDisplay displayComponent, Player player,
                                               TooltipFlag type, Consumer<Component> textConsumer, CallbackInfo ci) {
        SocketTooltip.appendLines((ItemStack) (Object) this, textConsumer, context.registries());
    }
}
