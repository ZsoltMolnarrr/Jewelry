package net.jewelry.neoforge.client;

import net.jewelry.JewelryMod;
import net.jewelry.client.JewelryModClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = JewelryMod.ID, value = Dist.CLIENT)
public class NeoForgeClientMod {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        JewelryMod.init();

        // Deduplicate jewelry tooltip lines — NeoForge game-bus event (replaces Fabric ItemTooltipCallback).
        NeoForge.EVENT_BUS.addListener(ItemTooltipEvent.class, tooltip ->
                JewelryModClient.removeTooltipDuplicates(tooltip.getItemStack(), tooltip.getToolTip()));
    }
}
