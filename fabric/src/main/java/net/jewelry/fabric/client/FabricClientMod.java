package net.jewelry.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.jewelry.client.GemCuttingScreen;
import net.jewelry.gems.GemCuttingScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.client.JewelryModClient;
import net.minecraft.client.render.BlockRenderLayer;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 1.21.11: `BlockRenderLayerMap` moved to `api.client.rendering.v1` and keys off the
        // `BlockRenderLayer` enum instead of a `RenderLayer` instance.
        BlockRenderLayerMap.putBlock(JewelryBlocks.JEWELERS_KIT.block(), BlockRenderLayer.CUTOUT);

        // Screen registration — Fabric API widens the vanilla-private HandledScreens.register (NeoForge: RegisterMenuScreensEvent).
        HandledScreens.register(GemCuttingScreenHandler.HANDLER_TYPE, GemCuttingScreen::new);

        // Deduplicate jewelry tooltip lines — Fabric API (NeoForge uses ItemTooltipEvent).
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) ->
                JewelryModClient.removeTooltipDuplicates(stack, lines));

        // Cut gem models need no registration here: 1.21.4+ discovers item-model definitions
        // (`assets/<ns>/items/gem_cut/*.json`) by itself, see `CustomModels`.
    }
}
