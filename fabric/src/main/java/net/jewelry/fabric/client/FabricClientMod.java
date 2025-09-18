package net.jewelry.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.client.JewelryModClient;
import net.minecraft.client.render.RenderLayer;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        JewelryModClient.init();
        BlockRenderLayerMap.INSTANCE.putBlock(JewelryBlocks.JEWELERS_KIT.block(), RenderLayer.getCutout());
    }
}