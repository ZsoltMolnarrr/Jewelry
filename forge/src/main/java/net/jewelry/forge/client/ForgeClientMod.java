package net.jewelry.forge.client;

import net.jewelry.client.JewelryModClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

/// Client-only wiring. Only touched behind `Dist.CLIENT` from `ForgeMod`, so there is no
/// `@EventBusSubscriber` and no class-loading of client types on a dedicated server.
///
/// The jeweler's kit renders cutout through the `"render_type": "minecraft:cutout"` field in its block
/// model (a Forge/NeoForge model extension), so no render-layer registration is needed here — the Fabric
/// module needs `BlockRenderLayerMap` only because Fabric ignores that field.
public final class ForgeClientMod {
    private ForgeClientMod() { }

    public static void register(IEventBus modBus) {
        // Deduplicate jewelry tooltip lines — Forge game-bus event (replaces Fabric ItemTooltipCallback).
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ItemTooltipEvent.class, event ->
                JewelryModClient.removeTooltipDuplicates(event.getItemStack(), event.getToolTip()));
    }
}
