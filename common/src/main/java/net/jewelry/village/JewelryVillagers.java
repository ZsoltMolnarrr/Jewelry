package net.jewelry.village;

import com.google.common.collect.ImmutableSet;
import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.items.Gems;
import net.jewelry.items.JewelryItems;
import net.jewelry.util.SoundHelper;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class JewelryVillagers {
    public static final String JEWELER = "jeweler";

    // These will be set by platform-specific code
    public static VillagerProfession JEWELER_PROFESSION;
    public static Identifier POI_ID = Identifier.of(JewelryMod.ID, JEWELER);
    public static final int POI_TICKET_COUNT = 1;
    public static final int POI_SEARCH_DISTANCE = 10;

    /// Registry key of {@link #JEWELER_PROFESSION} — since 1.21.2 both loaders' trade-registration APIs
    /// (Fabric `TradeOfferHelper.registerVillagerOffers`, NeoForge `VillagerTradesEvent#getType`) are
    /// keyed by it rather than by the profession object.
    public static final RegistryKey<VillagerProfession> JEWELER_PROFESSION_KEY =
            RegistryKey.of(RegistryKeys.VILLAGER_PROFESSION, Identifier.of(JewelryMod.ID, JEWELER));

    /// The jeweler's-kit workstation block states for the POI. Registration itself is loader-specific
    /// (Fabric: `PointOfInterestHelper`; NeoForge: a plain `Registry.register` of a `PointOfInterestType`)
    /// and lives in each platform's entrypoint; this only exposes the shared state set.
    public static Set<BlockState> poiBlockStates() {
        return ImmutableSet.copyOf(JewelryBlocks.JEWELERS_KIT.block().getStateManager().getStates());
    }

    public static VillagerProfession createProfession(String name, RegistryKey<PointOfInterestType> workStation) {
        var id = Identifier.of(JewelryMod.ID, name);
        return new VillagerProfession(
                // 1.21.11: the record's first component is the displayed name as a `Text`, not the id
                // string vanilla used to build `entity.minecraft.villager.<id>` from. Pass the key the
                // existing translation files already carry: `entity.minecraft.villager.jewelry.jeweler`.
                Text.translatable("entity.minecraft.villager." + id.getNamespace() + "." + id.getPath()),
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

    public static void registerVillagers() {
        // Register the profession only; trade-offer registration is loader-specific and lives in each
        // platform's entrypoint (Fabric `TradeOfferHelper` / NeoForge `VillagerTradesEvent`), consuming
        // the shared #createTrades() map.
        var workStation = RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), POI_ID);
        JEWELER_PROFESSION = Registry.register(Registries.VILLAGER_PROFESSION, Identifier.of(JewelryMod.ID, JEWELER), createProfession(JEWELER, workStation));
    }

    /// Emeralds per raw gem (64 = vanilla's stack cap; demand can't raise it further) and uses per restock.
    public static final int GEM_PRICE = 64;
    public static final int GEM_USES = 3;

    public static LinkedHashMap<Integer, List<TradeOffers.Factory>> createTrades() {
        LinkedHashMap<Integer, List<TradeOffers.Factory>> trades = new LinkedHashMap<>();

        trades.put(1, List.of(
                new JewelryTrades.Buy(Items.COPPER_INGOT, 8, 8, 4, 2),
                new JewelryTrades.Buy(Items.STRING, 7, 6, 4, 2),
                new JewelryTrades.Sell(JewelryItems.copper_ring.item(), 4, 1, 12, 4)
        ));
        trades.put(2, List.of(
                new JewelryTrades.Buy(Items.GOLD_INGOT, 7, 8, 8, 12),
                new JewelryTrades.Sell(JewelryItems.iron_ring.item(), 4, 1, 6, 10),
                new JewelryTrades.Sell(JewelryItems.gold_ring.item(), 18, 1, 6, 10)
        ));
        trades.put(3, List.of(
                new JewelryTrades.Buy(Items.DIAMOND, 1, 8, 30, 8),
                new JewelryTrades.Sell(JewelryItems.emerald_necklace.item(), 20, 1, 12, 30),
                new JewelryTrades.Sell(JewelryItems.diamond_necklace.item(), 25, 1, 12, 30),
                new JewelryTrades.Sell(JewelryItems.diamond_ring.item(), 20, 1, 12, 30)
        ));
        // Raw gems are the jeweler's endgame goods: a consistent but expensive source (the emerald cap is a
        // stack) that players cut at the Jeweler's Kit or craft into jewelry themselves — finished gem
        // jewelry is no longer sold, since it would undercut its own ingredient. Vanilla rolls two offers per
        // level from each pool, so a jeweler stocks two of the three gems at each of these levels.
        trades.put(4, List.of(
                new JewelryTrades.Sell(Gems.ruby.item(), GEM_PRICE, 1, GEM_USES, 40),
                new JewelryTrades.Sell(Gems.sapphire.item(), GEM_PRICE, 1, GEM_USES, 40),
                new JewelryTrades.Sell(Gems.jade.item(), GEM_PRICE, 1, GEM_USES, 40)
        ));
        trades.put(5, List.of(
                new JewelryTrades.Sell(Gems.topaz.item(), GEM_PRICE, 1, GEM_USES, 60),
                new JewelryTrades.Sell(Gems.citrine.item(), GEM_PRICE, 1, GEM_USES, 60),
                new JewelryTrades.Sell(Gems.tanzanite.item(), GEM_PRICE, 1, GEM_USES, 60)
        ));

        return trades;
    }
}
