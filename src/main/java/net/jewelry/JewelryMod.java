package net.jewelry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.jewelry.api.AttributeResolver;
import net.jewelry.config.Default;
import net.jewelry.config.ItemConfig;
import net.jewelry.config.WorldGenConfig;
import net.jewelry.items.*;
import net.jewelry.village.JewelryVillagers;
import net.jewelry.worldgen.OreGeneration;
import net.jewelry.worldgen.VillageGeneration;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.tinyconfig.ConfigManager;

public class JewelryMod implements ModInitializer {
    public static final String ID = "jewelry";

    public static ConfigManager<ItemConfig> itemConfig = new ConfigManager<>
            ("items", Default.items)
            .builder()
            .setDirectory(ID)
            .sanitize(true)
            .build();

    public static ConfigManager<WorldGenConfig> worldGenConfig = new ConfigManager<>
            ("world_gen", Default.worldGen)
            .builder()
            .setDirectory(ID)
            .sanitize(true)
            .build();

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        AttributeResolver.setup();
        itemConfig.refresh();
        worldGenConfig.refresh();

        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.JEWELRY);
        JewelryBlocks.register();
        Gems.register();
        JewelryItems.register(itemConfig.value);
        itemConfig.save();

        JewelryVillagers.register();

        OreGeneration.register();
        // ServerLifecycleEvents.SERVER_STARTING.register(VillageGeneration::init);
    }
}
