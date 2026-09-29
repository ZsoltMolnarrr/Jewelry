package net.jewelry.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.jewelry.client.CustomModels;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.MissingItemModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemModelResolver.class)
public abstract class ItemModelManagerMixin {
    // 26.1: the resolver asks the `ModelManager` through this private getter (no more function fields).
    @Shadow
    protected abstract ItemModel getItemModel(Identifier modelId);

    /// Swap in the stack's custom item model (explicit `jewelry:item_model` component, or the cut gem's).
    /// 1.21.4+: a stack renders through the item-model definition its `minecraft:item_model` component
    /// names, so the swap replaces that id as it is read. A definition that does not exist leaves the
    /// stack on its own model (the raw gem's).
    @ModifyExpressionValue(method = "appendItemLayers",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    private Object update_itemModel_Jewelry(Object original, ItemStackRenderState renderState, ItemStack stack) {
        var custom = CustomModels.definitionIdOf(stack);
        if (custom == null || getItemModel(custom) instanceof MissingItemModel) {
            return original;
        }
        return custom;
    }
}
