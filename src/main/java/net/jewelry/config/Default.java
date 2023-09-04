package net.jewelry.config;

import java.util.ArrayList;
import java.util.List;

public class Default {
    public static final ItemConfig items;
    public static final WorldGenConfig worldGen;

    static {
        items = new ItemConfig();
        worldGen = new WorldGenConfig();
        var structureWeight = 3;
        worldGen.entries = new ArrayList<>(List.of(
                new WorldGenConfig.Entry("minecraft:village/desert/houses", "jewelry:village/desert/jewelry_shop", structureWeight),
                new WorldGenConfig.Entry("minecraft:village/savanna/houses", "jewelry:village/savanna/jewelry_shop", structureWeight),
                new WorldGenConfig.Entry("minecraft:village/plains/houses", "jewelry:village/plains/jewelry_shop", structureWeight),
                new WorldGenConfig.Entry("minecraft:village/taiga/houses", "jewelry:village/taiga/jewelry_shop", structureWeight),
                new WorldGenConfig.Entry("minecraft:village/snowy/houses", "jewelry:village/snowy/jewelry_shop", structureWeight)
        ));
    }
}
