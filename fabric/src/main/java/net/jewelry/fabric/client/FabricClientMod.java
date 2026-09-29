package net.jewelry.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.jewelry.client.GemCuttingScreen;
import net.jewelry.client.JewelryModClient;
import net.jewelry.gems.GemCuttingScreenHandler;
import net.minecraft.client.gui.screens.MenuScreens;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 26.1 removed `BlockRenderLayerMap`: the chunk-section layer is derived from the block model.

        // Screen registration — Fabric API widens the vanilla-private MenuScreens.register (NeoForge: RegisterMenuScreensEvent).
        MenuScreens.register(GemCuttingScreenHandler.HANDLER_TYPE, GemCuttingScreen::new);

        // Deduplicate jewelry tooltip lines — Fabric API (NeoForge uses ItemTooltipEvent).
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) ->
                JewelryModClient.removeTooltipDuplicates(stack, lines));

        // Cut gem models need no registration here: 1.21.4+ discovers item-model definitions
        // (`assets/<ns>/items/gem_cut/*.json`) by itself, see `CustomModels`.
    }
}
