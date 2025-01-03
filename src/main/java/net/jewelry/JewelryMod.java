package net.jewelry;

import net.fabric_extras.structure_pool.api.StructurePoolAPI;
import net.fabric_extras.structure_pool.api.StructurePoolConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.config.Default;
import net.jewelry.config.ItemConfig;
import net.jewelry.items.Gems;
import net.jewelry.items.Group;
import net.jewelry.items.JewelryItems;
import net.jewelry.util.SoundHelper;
import net.jewelry.village.JewelryVillagers;
import net.jewelry.worldgen.OreGeneration;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.tinyconfig.ConfigManager;

public class JewelryMod implements ModInitializer {
    public static final String ID = "jewelry";

    public static ConfigManager<ItemConfig> itemConfig = new ConfigManager<>
            ("items_v6", Default.items)
            .builder()
            .setDirectory(ID)
            .sanitize(true)
            .build();

    public static ConfigManager<StructurePoolConfig> villageConfig = new ConfigManager<>
            ("villages", Default.villages)
            .builder()
            .setDirectory(ID)
            .sanitize(true)
            .build();

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        itemConfig.refresh();
        villageConfig.refresh();

        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.JEWELRY);
        JewelryBlocks.register();
        Gems.register();
        JewelryItems.register(itemConfig.value);
        itemConfig.save();

        JewelryVillagers.register();
        SoundHelper.register();

        OreGeneration.register();

        if (!FabricLoader.getInstance().isModLoaded("lithostitched")) {
            // Only inject the village if the Lithostitched is not present
            StructurePoolAPI.injectAll(villageConfig.value);
        }
    }
}
