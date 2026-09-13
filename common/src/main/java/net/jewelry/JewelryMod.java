package net.jewelry;

import net.fabric_extras.structure_pool.api.StructurePoolAPI;
import net.fabric_extras.structure_pool.api.StructurePoolConfig;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.config.Default;
import net.jewelry.config.ItemConfig;
import net.jewelry.gems.GemComponents;
import net.jewelry.gems.GemCut;
import net.jewelry.gems.GemCutRegistry;
import net.jewelry.gems.GemCuttingScreenHandler;
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
            StructurePoolAPI.injectAll(JewelryMod.villageConfig.value);
        }
    }

    /// Synced datapack registries. Called exactly once per loader, before the loader's registry
    /// events fire (NeoForge buffers it until `DataPackRegistryEvent.NewRegistry`).
    public static void registerDataRegistries() {
        Platform.util().registerSyncedDataRegistry(GemCutRegistry.KEY, GemCut.CODEC, GemCut.CODEC);
    }

    public static void registerComponents() {
        GemComponents.register();
    }

    public static void registerScreenHandlers() {
        Registry.register(Registries.SCREEN_HANDLER, GemCuttingScreenHandler.ID, GemCuttingScreenHandler.HANDLER_TYPE);
    }

    public static void registerSounds() {
        SoundHelper.register();
    }

    public static void registerBlocks() {
        JewelryBlocks.register();
    }

    public static void registerItems() {
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.JEWELRY);
        Registry.register(Registries.ITEM_GROUP, Group.GEMS_KEY, Group.GEMS);
        Gems.register();
        JewelryItems.register(itemConfig.value);
        itemConfig.save();
    }

    public static void registerVillagers() {
        JewelryVillagers.registerVillagers();
    }
}
