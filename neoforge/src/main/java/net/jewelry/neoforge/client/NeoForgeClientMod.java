package net.jewelry.neoforge.client;

import net.jewelry.JewelryMod;
import net.jewelry.client.JewelryModClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.jewelry.client.GemCuttingScreen;
import net.jewelry.gems.GemCuttingScreenHandler;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.jewelry.client.CustomModels;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ModelIdentifier;

@EventBusSubscriber(modid = JewelryMod.ID, value = Dist.CLIENT)
public class NeoForgeClientMod {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        JewelryMod.init();

        // Deduplicate jewelry tooltip lines — NeoForge game-bus event (replaces Fabric ItemTooltipCallback).
        NeoForge.EVENT_BUS.addListener(ItemTooltipEvent.class, tooltip ->
                JewelryModClient.removeTooltipDuplicates(tooltip.getItemStack(), tooltip.getToolTip()));
    }

    /// Cut gem models (`models/item/gem_cut/*.json`) are standalone, so they must be registered for baking.
    /// NeoForge bakes them under `ModelIdentifier.standalone(id)`; the renderer mixin looks them up the same way.
    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(GemCuttingScreenHandler.HANDLER_TYPE, GemCuttingScreen::new);
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        var discovered = CustomModels.discover(MinecraftClient.getInstance().getResourceManager());
        for (var id : discovered) {
            event.register(ModelIdentifier.standalone(id));
        }
    }
}
