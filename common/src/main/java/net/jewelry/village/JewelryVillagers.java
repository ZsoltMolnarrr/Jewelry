package net.jewelry.village;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.items.JewelryItems;
import net.jewelry.util.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.LinkedHashMap;
import java.util.List;

public class JewelryVillagers {
    public static final String JEWELER = "jeweler";

    // These will be set by platform-specific code
    public static VillagerProfession JEWELER_PROFESSION;
    public static Identifier POI_ID = Identifier.of(JewelryMod.ID, JEWELER);

    public static VillagerProfession createProfession(String name, RegistryKey<PointOfInterestType> workStation) {
        var id = Identifier.of(JewelryMod.ID, name);
        return new VillagerProfession(
                id.toString(),
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                ImmutableSet.of(),
                ImmutableSet.of(),
                SoundHelper.JEWELRY_WORKBENCH
        );
    }

    public static void registerPOI() {
        PointOfInterestHelper.register(POI_ID,
                1, 10, ImmutableSet.copyOf(JewelryBlocks.JEWELERS_KIT.block().getStateManager().getStates())
        );
    }

    public static void registerVillagers() {
        // Register profession and trades
        var workStation = RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), POI_ID);
        JEWELER_PROFESSION = Registry.register(Registries.VILLAGER_PROFESSION, Identifier.of(JewelryMod.ID, JEWELER), createProfession(JEWELER, workStation));

        var trades = JewelryVillagers.createTrades();
        for (var entry : trades.entrySet()) {
            TradeOfferHelper.registerVillagerOffers(JewelryVillagers.JEWELER_PROFESSION, entry.getKey(), factories -> {
                factories.addAll(entry.getValue());
            });
        }
    }

    public static LinkedHashMap<Integer, List<TradeOffers.Factory>> createTrades() {
        LinkedHashMap<Integer, List<TradeOffers.Factory>> trades = new LinkedHashMap<>();

        trades.put(1, List.of(
                new TradeOffers.BuyItemFactory(Items.COPPER_INGOT, 8, 8, 3, 2),
                new TradeOffers.BuyItemFactory(Items.STRING, 7, 6, 3, 2),
                new TradeOffers.SellItemFactory(JewelryItems.copper_ring.item(), 4, 1, 12, 4)
        ));
        trades.put(2, List.of(
                new TradeOffers.BuyItemFactory(Items.GOLD_INGOT, 7, 8, 2, 8),
                new TradeOffers.SellItemFactory(JewelryItems.iron_ring.item(), 4, 1, 6, 5),
                new TradeOffers.SellItemFactory(JewelryItems.gold_ring.item(), 18, 1, 6, 5)
        ));
        trades.put(3, List.of(
                new TradeOffers.BuyItemFactory(Items.DIAMOND, 1, 12, 10, 10),
                new TradeOffers.SellItemFactory(JewelryItems.emerald_necklace.item(), 20, 1, 12, 10),
                new TradeOffers.SellItemFactory(JewelryItems.diamond_necklace.item(), 25, 1, 12, 10)
        ));
        trades.put(4, List.of(
                new TradeOffers.SellItemFactory(JewelryItems.ruby_ring.item(), 35, 1, 5, 15),
                new TradeOffers.SellItemFactory(JewelryItems.topaz_ring.item(), 35, 1, 5, 15),
                new TradeOffers.SellItemFactory(JewelryItems.citrine_ring.item(), 35, 1, 5, 15),
                new TradeOffers.SellItemFactory(JewelryItems.jade_ring.item(), 35, 1, 5, 15),
                new TradeOffers.SellItemFactory(JewelryItems.sapphire_ring.item(), 35, 1, 5, 13),
                new TradeOffers.SellItemFactory(JewelryItems.tanzanite_ring.item(), 35, 1, 5, 13)
        ));
        trades.put(5, List.of(
                new TradeOffers.SellItemFactory(JewelryItems.ruby_necklace.item(), 45, 1, 3, 15),
                new TradeOffers.SellItemFactory(JewelryItems.topaz_necklace.item(), 45, 1, 3, 15),
                new TradeOffers.SellItemFactory(JewelryItems.citrine_necklace.item(), 45, 1, 3, 15),
                new TradeOffers.SellItemFactory(JewelryItems.jade_necklace.item(), 45, 1, 3, 15),
                new TradeOffers.SellItemFactory(JewelryItems.sapphire_necklace.item(), 45, 1, 3, 15),
                new TradeOffers.SellItemFactory(JewelryItems.tanzanite_necklace.item(), 45, 1, 3, 15)
        ));

        return trades;
    }
}
