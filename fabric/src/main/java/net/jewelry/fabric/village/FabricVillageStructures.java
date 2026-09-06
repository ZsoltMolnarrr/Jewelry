package net.jewelry.fabric.village;

import net.fabric_extras.structure_pool.api.StructurePoolAPI;
import net.fabric_extras.structure_pool.api.StructurePoolConfig;
import net.jewelry.JewelryMod;
import net.jewelry.Platform;
import net.jewelry.village.VillageStructures;
import net.tiny_config.ConfigManager;

import java.util.List;

/// Fabric-only implementation of {@link VillageStructures}: StructurePoolAPI has no Forge artifact on
/// 1.20.1, so both the `config/jewelry/villages.json` config and the injection call live here.
public final class FabricVillageStructures {
    private FabricVillageStructures() { }

    public static final ConfigManager<StructurePoolConfig> villageConfig = new ConfigManager<StructurePoolConfig>
            ("villages", defaults())
            .builder()
            .setDirectory(JewelryMod.ID)
            .sanitize(true)
            .build();

    /// Installs the injector and loads (or writes) the config file. Called from the Fabric entrypoint
    /// before {@code JewelryMod.init()}.
    public static void install() {
        villageConfig.refresh();
        VillageStructures.injector = () -> {
            if (!Platform.util().isModLoaded("lithostitched")) {
                // Only inject the shop if Lithostitched is not present
                StructurePoolAPI.injectAll(villageConfig.value);
            }
        };
    }

    private static StructurePoolConfig defaults() {
        var config = new StructurePoolConfig();
        var weight = 2;
        var limit = 1;
        config.entries.addAll(List.of(
                new StructurePoolConfig.Entry("minecraft:village/desert/houses", "jewelry:village/desert/jewelry_shop", weight, limit),
                new StructurePoolConfig.Entry("minecraft:village/savanna/houses", "jewelry:village/savanna/jewelry_shop", weight, limit),
                new StructurePoolConfig.Entry("minecraft:village/plains/houses", "jewelry:village/plains/jewelry_shop", weight, limit),
                new StructurePoolConfig.Entry("minecraft:village/taiga/houses", "jewelry:village/taiga/jewelry_shop", weight, limit),
                new StructurePoolConfig.Entry("minecraft:village/snowy/houses", "jewelry:village/snowy/jewelry_shop", weight, limit)
        ));
        return config;
    }
}
