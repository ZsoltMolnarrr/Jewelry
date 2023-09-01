package net.jewelry;

import net.fabricmc.api.ModInitializer;
import net.jewelry.api.AttributeResolver;
import net.jewelry.config.Default;
import net.jewelry.config.ItemConfig;
import net.jewelry.internals.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.tinyconfig.ConfigManager;

public class JewelryMod implements ModInitializer {
    public static final String ID = "jewelry";

    public static ConfigManager<ItemConfig> itemConfig = new ConfigManager<ItemConfig>
            ("items", Default.items)
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

        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.JEWELRY);
        JewelryBlocks.register();
        Gems.register();
        JewelryItems.register(itemConfig.value);
        OreGeneration.register();
    }
}
