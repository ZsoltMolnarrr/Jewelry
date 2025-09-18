package net.jewelry.config;

import net.fabric_extras.structure_pool.api.StructurePoolConfig;
import net.jewelry.items.JewelryItems;

import java.util.ArrayList;
import java.util.List;

public class Default {
    public static final ItemConfig items;
    public static final StructurePoolConfig villages;

    static {
        items = new ItemConfig();
        villages = new StructurePoolConfig();
        var weight = 2;
        var limit = 1;
        villages.entries = new ArrayList<>(List.of(
                new StructurePoolConfig.Entry("minecraft:village/desert/houses", "jewelry:village/desert/jewelry_shop", weight, limit),
                new StructurePoolConfig.Entry("minecraft:village/savanna/houses", "jewelry:village/savanna/jewelry_shop", weight, limit),
                new StructurePoolConfig.Entry("minecraft:village/plains/houses", "jewelry:village/plains/jewelry_shop", weight, limit),
                new StructurePoolConfig.Entry("minecraft:village/taiga/houses", "jewelry:village/taiga/jewelry_shop", weight, limit),
                new StructurePoolConfig.Entry("minecraft:village/snowy/houses", "jewelry:village/snowy/jewelry_shop", weight, limit)
        ));
    }
}
