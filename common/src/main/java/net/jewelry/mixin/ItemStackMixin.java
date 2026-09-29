package net.jewelry.mixin;

import net.jewelry.gems.GemSockets;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
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
    @Inject(method = "applyAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V", at = @At("TAIL"))
    private void applyAttributeModifiers_TAIL_Jewelry(EquipmentSlot slot,
                                                      BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> consumer,
                                                      CallbackInfo ci) {
        GemSockets.applyModifiers((ItemStack) (Object) this, slot, consumer);
    }
}
