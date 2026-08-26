package net.jewelry.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.client.JewelryModClient;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 1.21.11: `BlockRenderLayerMap` moved to `api.client.rendering.v1` and keys off the
        // `BlockRenderLayer` enum instead of a `RenderLayer` instance.
        BlockRenderLayerMap.putBlock(JewelryBlocks.JEWELERS_KIT.block(), ChunkSectionLayer.CUTOUT);

        // Deduplicate jewelry tooltip lines — Fabric API (NeoForge uses ItemTooltipEvent).
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) ->
                JewelryModClient.removeTooltipDuplicates(stack, lines));
    }
}
