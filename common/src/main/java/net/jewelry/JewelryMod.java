package net.jewelry;

import net.fabric_extras.structure_pool.api.StructurePoolAPI;
import net.fabric_extras.structure_pool.api.StructurePoolConfig;
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
            ("items_v8", Default.items)
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
            // Only inject the shop if Lithostitched is not present - otherwise the data-driven
            // worldgen modifiers in `resources/data/jewelry` already do it.
            //
            // `injectAll` only *queues* the entries; StructurePoolAPI's own entrypoint applies them
            // when the server starts (Fabric SERVER_STARTING / Forge ServerAboutToStartEvent, both
            // before the spawn region generates). The queue is deliberately never cleared, so this
            // must be called exactly once, here at mod init - never per world load.
            StructurePoolAPI.injectAll(villageConfig.value);
        }
    }

    public static void registerSounds() {
        SoundHelper.register();
    }

    public static void registerBlocks() {
        JewelryBlocks.register();
    }

    /// Builds every jewelry item from the item config and writes back whatever the config was missing.
    /// Creation only — nothing is registered here, so a loader that registers the items itself calls this
    /// first and then iterates `JewelryItems.all`. Must run inside the ITEM registration window (see
    /// {@link JewelryItems#create}).
    public static void createJewelryItems() {
        JewelryItems.create(itemConfig.value);
        itemConfig.save();
    }

    public static void registerItems() {
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.JEWELRY);
        JewelryBlocks.registerBlockItems();
        Gems.register();
        JewelryItems.register(itemConfig.value);
        itemConfig.save();
    }

    public static void registerVillagers() {
        JewelryVillagers.registerVillagers();
    }
}
