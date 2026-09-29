package net.jewelry.mixin.client;

import net.jewelry.gems.SocketTooltip;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
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
    @Inject(method = "appendTooltip",
            at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
                    target = "Lnet/minecraft/component/DataComponentTypes;DYED_COLOR:Lnet/minecraft/component/ComponentType;"))
    private void appendTooltip_sockets_Jewelry(Item.TooltipContext context, TooltipDisplayComponent displayComponent, PlayerEntity player,
                                               TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci) {
        SocketTooltip.appendLines((ItemStack) (Object) this, textConsumer, context.getRegistryLookup());
    }
}
