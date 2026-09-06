package net.jewelry.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.client.JewelryModClient;
import net.minecraft.client.render.RenderLayer;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(JewelryBlocks.JEWELERS_KIT.block(), RenderLayer.getCutout());

        // Deduplicate jewelry tooltip lines — Fabric API (Forge uses ItemTooltipEvent).
        // Fabric API 0.92's callback is the 3-arg (stack, context, lines) shape.
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, lines) ->
                JewelryModClient.removeTooltipDuplicates(stack, lines));
    }
}
