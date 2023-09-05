package net.jewelry.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.jewelry.blocks.JewelryBlocks;
import net.minecraft.client.render.RenderLayer;

public class JewelryModClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(JewelryBlocks.JEWELERS_KIT.block(), RenderLayer.getCutout());
    }
}
