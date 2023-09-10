package net.jewelry.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.jewelry.api.JewelryItem;
import net.jewelry.blocks.JewelryBlocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class JewelryModClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(JewelryBlocks.JEWELERS_KIT.block(), RenderLayer.getCutout());
        ItemTooltipCallback.EVENT.register((itemStack, context, lines) -> {
            removeTooltipDuplicates(itemStack, lines);
        });
    }

    private static void removeTooltipDuplicates(ItemStack itemStack, List<Text> tooltip) {
        if (itemStack.getItem() instanceof JewelryItem) {
            for (int i = 0; i < tooltip.size(); i++) {
                var text = tooltip.get(i);
                for (int j = i + 1; j < tooltip.size(); j++) {
                    if (text.getString().equals(tooltip.get(j).getString())) {
                        tooltip.remove(j);
                        j--;
                    }
                }
            }
        }
    }
}
