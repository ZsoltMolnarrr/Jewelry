package net.jewelry.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.jewelry.client.CustomModels;
import net.jewelry.client.GemCuttingScreen;
import net.jewelry.gems.GemCuttingScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.MinecraftClient;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.client.JewelryModClient;
import net.minecraft.client.render.RenderLayer;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(JewelryBlocks.JEWELERS_KIT.block(), RenderLayer.getCutout());

        // Screen registration — Fabric API widens the vanilla-private HandledScreens.register (NeoForge: RegisterMenuScreensEvent).
        HandledScreens.register(GemCuttingScreenHandler.HANDLER_TYPE, GemCuttingScreen::new);

        // Deduplicate jewelry tooltip lines — Fabric API (NeoForge uses ItemTooltipEvent).
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) ->
                JewelryModClient.removeTooltipDuplicates(stack, lines));

        // Cut gem models (`models/item/gem_cut/*.json`) are standalone, so they must be registered for baking.
        ModelLoadingPlugin.register(context ->
                context.addModels(CustomModels.discover(MinecraftClient.getInstance().getResourceManager())));
    }
}
