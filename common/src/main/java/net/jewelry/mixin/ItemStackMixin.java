package net.jewelry.mixin;

import net.jewelry.gems.GemSockets;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    /// Socketed gems contribute their modifiers alongside the item's own and its enchantments' — but only
    /// on the equipment path (`EquipmentSlot`), which entities use when gear changes. The tooltip's
    /// `AttributeModifierSlot` variant is left alone so gem bonuses show on their socket lines instead
    /// of being merged into the "When on Body" section.
    @Inject(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V", at = @At("TAIL"))
    private void applyAttributeModifiers_TAIL_Jewelry(EquipmentSlot slot,
                                                      BiConsumer<Holder<Attribute>, AttributeModifier> consumer,
                                                      CallbackInfo ci) {
        GemSockets.applyModifiers((ItemStack) (Object) this, slot, consumer);
    }
}
