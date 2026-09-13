package net.jewelry.mixin.client;

import net.jewelry.Platform;
import net.jewelry.client.CustomModels;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Shadow
    @Final
    private ItemModels models;

    /// Swap in the stack's custom model (explicit component, or the cut gem's model), same as SpellEngine.
    /// Standalone models are addressed differently per loader: Fabric API resolves the plain id,
    /// NeoForge needs the `standalone` ModelIdentifier wrapping it was registered under.
    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void getModel_HEAD_Jewelry(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        var modelId = CustomModels.modelIdOf(stack);
        if (modelId == null) {
            return;
        }
        var manager = models.getModelManager();
        BakedModel model = Platform.util().isFabric()
                ? manager.getModel(modelId)
                : manager.getModel(new ModelIdentifier(modelId, "standalone"));
        if (model != null && model != manager.getMissingModel()) {
            cir.setReturnValue(model);
        }
    }
}
