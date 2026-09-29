package net.jewelry;

import net.rpg_foundation.structure_pool.api.StructurePoolAPI;
import net.rpg_foundation.structure_pool.api.StructurePoolConfig;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.config.Default;
import net.jewelry.config.FeaturesConfig;
import net.jewelry.config.ItemConfig;
import net.jewelry.gems.GemComponents;
import net.jewelry.gems.GemCut;
import net.jewelry.gems.GemCutRegistry;
import net.jewelry.gems.GemCuttingScreenHandler;
import net.jewelry.gems.SocketMounts;
import net.jewelry.gems.SocketType;
import net.jewelry.gems.SocketTypeRegistry;
import net.jewelry.items.Gems;
import net.jewelry.items.Group;
import net.jewelry.items.JewelryItems;
import net.jewelry.util.SoundHelper;
import net.jewelry.village.JewelryVillagers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
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

    public static ConfigManager<FeaturesConfig> featuresConfig = new ConfigManager<>
            ("features", new FeaturesConfig())
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
        featuresConfig.refresh();
        if (!Platform.util().isModLoaded("lithostitched")) {
            StructurePoolAPI.injectAll(JewelryMod.villageConfig.value);
        }
    }

    /// Synced datapack registries. Called exactly once per loader, before the loader's registry
    /// events fire (NeoForge buffers it until `DataPackRegistryEvent.NewRegistry`).
    public static void registerDataRegistries() {
        Platform.util().registerSyncedDataRegistry(GemCutRegistry.KEY, GemCut.CODEC, GemCut.CODEC);
        Platform.util().registerSyncedDataRegistry(SocketTypeRegistry.KEY, SocketType.CODEC, SocketType.CODEC);
    }

    public static void registerScreenHandlers() {
        Registry.register(BuiltInRegistries.MENU, GemCuttingScreenHandler.ID, GemCuttingScreenHandler.HANDLER_TYPE);
    }

    public static void registerSounds() {
        SoundHelper.register();
    }

    public static void registerBlocks() {
        JewelryBlocks.register();
    }

    public static void registerItems() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Group.KEY, Group.JEWELRY);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Group.GEMS_KEY, Group.GEMS);
        Gems.register();
        SocketMounts.register();
        JewelryItems.register(itemConfig.value);
        itemConfig.save();
    }

    public static void registerVillagers() {
        JewelryVillagers.registerVillagers();
    }
}
