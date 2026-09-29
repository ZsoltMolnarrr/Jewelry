package net.jewelry.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.jewelry.client.CustomModels;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.MissingItemModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(ItemModelManager.class)
public class ItemModelManagerMixin {
    @Shadow
    @Final
    private Function<Identifier, ItemModel> modelGetter;

    /// Swap in the stack's custom item model (explicit `jewelry:item_model` component, or the cut gem's).
    /// 1.21.4+: a stack renders through the item-model definition its `minecraft:item_model` component
    /// names, so the swap replaces that id as it is read. A definition that does not exist leaves the
    /// stack on its own model (the raw gem's).
    @ModifyExpressionValue(method = "update",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;get(Lnet/minecraft/component/ComponentType;)Ljava/lang/Object;"))
    private Object update_itemModel_Jewelry(Object original, ItemRenderState renderState, ItemStack stack) {
        var custom = CustomModels.definitionIdOf(stack);
        if (custom == null || modelGetter.apply(custom) instanceof MissingItemModel) {
            return original;
        }
        return custom;
    }
}
