package net.jewelry;

import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.config.Default;
import net.jewelry.config.ItemConfig;
import net.jewelry.items.Gems;
import net.jewelry.items.Group;
import net.jewelry.items.JewelryItems;
import net.jewelry.util.SoundHelper;
import net.jewelry.village.JewelryVillagers;
import net.jewelry.village.VillageStructures;
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

    /**
     * Runs the mod initializer.
     */
    public static void init() {
        itemConfig.refresh();
        // Vanilla-village injection is Fabric-only on 1.20.1 (StructurePoolAPI has no Forge build);
        // the injector, and the `config/jewelry/villages.json` it reads, live in the Fabric module.
        VillageStructures.injectIfAvailable();
    }

    public static void registerSounds() {
        SoundHelper.register();
    }

    public static void registerBlocks() {
        JewelryBlocks.register();
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
