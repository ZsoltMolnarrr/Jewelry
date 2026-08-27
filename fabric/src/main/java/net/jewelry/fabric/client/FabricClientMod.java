package net.jewelry.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.jewelry.client.JewelryModClient;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 26.1 removed `BlockRenderLayerMap`: the chunk-section layer is derived from the block model.

        // Deduplicate jewelry tooltip lines — Fabric API (NeoForge uses ItemTooltipEvent).
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) ->
                JewelryModClient.removeTooltipDuplicates(stack, lines));
    }
}
