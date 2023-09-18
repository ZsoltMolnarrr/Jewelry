package net.jewelry.config;

import net.jewelry.items.JewelryItems;

import java.util.ArrayList;
import java.util.List;

public class Default {
    public static final ItemConfig items;
    public static final WorldGenConfig worldGen;
    public static final LootConfig loot;

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
        loot = new LootConfig();

        var jewelry_tier_0 = "jewelry_tier_0";
        loot.item_groups.put(jewelry_tier_0, new LootConfig.ItemGroup(List.of(
                JewelryItems.copper_ring.id().toString(),
                JewelryItems.iron_ring.id().toString(),
                JewelryItems.gold_ring.id().toString()),
                1)
                .chance(0.3F)
        );

        var jewelry_tier_1 = "jewelry_tier_1";
        loot.item_groups.put(jewelry_tier_1, new LootConfig.ItemGroup(List.of(
                JewelryItems.ruby_ring.id().toString(),
                JewelryItems.topaz_ring.id().toString(),
                JewelryItems.citrine_ring.id().toString(),
                JewelryItems.jade_ring.id().toString(),
                JewelryItems.sapphire_ring.id().toString(),
                JewelryItems.tanzanite_ring.id().toString(),

                JewelryItems.ruby_necklace.id().toString(),
                JewelryItems.topaz_necklace.id().toString(),
                JewelryItems.citrine_necklace.id().toString(),
                JewelryItems.jade_necklace.id().toString(),
                JewelryItems.sapphire_necklace.id().toString(),
                JewelryItems.tanzanite_necklace.id().toString()
                ),
                1)
                .chance(0.2F)
        );

        var jewelry_tier_2 = "jewelry_tier_2";
        loot.item_groups.put(jewelry_tier_2, new LootConfig.ItemGroup(List.of(
                JewelryItems.netherite_ruby_ring.id().toString(),
                JewelryItems.netherite_topaz_ring.id().toString(),
                JewelryItems.netherite_citrine_ring.id().toString(),
                JewelryItems.netherite_jade_ring.id().toString(),
                JewelryItems.netherite_sapphire_ring.id().toString(),
                JewelryItems.netherite_tanzanite_ring.id().toString(),

                JewelryItems.netherite_ruby_necklace.id().toString(),
                JewelryItems.netherite_topaz_necklace.id().toString(),
                JewelryItems.netherite_citrine_necklace.id().toString(),
                JewelryItems.netherite_jade_necklace.id().toString(),
                JewelryItems.netherite_sapphire_necklace.id().toString(),
                JewelryItems.netherite_tanzanite_necklace.id().toString()
                ),
                1)
                .chance(0.2F)
        );

        var jewelry_tier_3 = "jewelry_tier_3";
        loot.item_groups.put(jewelry_tier_3, new LootConfig.ItemGroup(List.of(
                JewelryItems.unique_attack_ring.id().toString(),
                JewelryItems.unique_attack_necklace.id().toString(),
                JewelryItems.unique_dex_ring.id().toString(),
                JewelryItems.unique_dex_necklace.id().toString(),
                JewelryItems.unique_tank_ring.id().toString(),
                JewelryItems.unique_tank_necklace.id().toString(),
                JewelryItems.unique_archer_ring.id().toString(),
                JewelryItems.unique_archer_necklace.id().toString(),
                JewelryItems.unique_arcane_ring.id().toString(),
                JewelryItems.unique_arcane_necklace.id().toString(),
                JewelryItems.unique_fire_ring.id().toString(),
                JewelryItems.unique_fire_necklace.id().toString(),
                JewelryItems.unique_frost_ring.id().toString(),
                JewelryItems.unique_frost_necklace.id().toString(),
                JewelryItems.unique_healing_ring.id().toString(),
                JewelryItems.unique_healing_necklace.id().toString()
//                JewelryItems.unique_lightning_ring.id().toString(),
//                JewelryItems.unique_lightning_necklace.id().toString(),
//                JewelryItems.unique_soul_ring.id().toString(),
//                JewelryItems.unique_soul_necklace.id().toString()
                ), 1)
                .chance(0.5F)
        );

        List.of("minecraft:chests/abandoned_mineshaft",
                        "minecraft:chests/igloo_chest",
                        "minecraft:chests/shipwreck_supply",
                        "minecraft:chests/jungle_temple",
                        "minecraft:chests/desert_pyramid",
                        "minecraft:chests/simple_dungeon")
                .forEach(id -> loot.loot_tables.put(id, List.of(jewelry_tier_0)));

        List.of("minecraft:chests/stronghold_library",
                        "minecraft:chests/underwater_ruin_big",
                        "minecraft:chests/ancient_city",
                        "minecraft:chests/woodland_mansion")
                .forEach(id -> loot.loot_tables.put(id, List.of(jewelry_tier_1)));

        List.of("minecraft:chests/bastion_treasure")
                .forEach(id -> loot.loot_tables.put(id, List.of(jewelry_tier_2)));

        List.of("minecraft:chests/end_city_treasure")
                .forEach(id -> loot.loot_tables.put(id, List.of(jewelry_tier_3)));
    }
}
