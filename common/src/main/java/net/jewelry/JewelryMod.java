package net.jewelry;

import net.rpg_foundation.structure_pool.api.StructurePoolAPI;
import net.rpg_foundation.structure_pool.api.StructurePoolConfig;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.config.Default;
import net.jewelry.config.ItemConfig;
import net.jewelry.items.Gems;
import net.jewelry.items.Group;
import net.jewelry.items.JewelryItems;
import net.jewelry.util.SoundHelper;
import net.jewelry.village.JewelryVillagers;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.tiny_config.ConfigManager;

public class JewelryMod {
    public static final String ID = "jewelry";

    public static ConfigManager<ItemConfig> itemConfig = new ConfigManager<>
            ("items_v9", Default.items)
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
    public static void init() {
        itemConfig.refresh();
        villageConfig.refresh();
        if (!Platform.util().isModLoaded("lithostitched")) {
            StructurePoolAPI.injectAll(JewelryMod.villageConfig.value);
        }
    }

    public static void registerSounds() {
        SoundHelper.register();
    }

    public static void registerBlocks() {
        JewelryBlocks.register();
    }

    public static void registerItems() {
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.JEWELRY);
        Gems.register();
        JewelryItems.register(itemConfig.value);
        itemConfig.save();
    }

    public static void registerVillagers() {
        JewelryVillagers.registerVillagers();
    }
}
