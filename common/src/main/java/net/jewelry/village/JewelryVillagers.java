package net.jewelry.village;

import com.google.common.collect.ImmutableSet;
import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.items.JewelryItems;
import net.jewelry.util.SoundHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class JewelryVillagers {
    public static final String JEWELER = "jeweler";
    public static final Identifier JEWELER_ID = new Identifier(JewelryMod.ID, JEWELER);

    // These will be set by platform-specific code
    public static VillagerProfession JEWELER_PROFESSION;
    public static Identifier POI_ID = new Identifier(JewelryMod.ID, JEWELER);
    public static final int POI_TICKET_COUNT = 1;
    public static final int POI_SEARCH_DISTANCE = 10;

    /// The jeweler's-kit workstation block states for the POI. Registration itself is loader-specific
    /// (Fabric: `PointOfInterestHelper`; Forge: a `PointOfInterestType` handed to the `RegisterEvent`
    /// helper) and lives in each platform's entrypoint; this only exposes the shared state set.
    public static Set<BlockState> poiBlockStates() {
        return ImmutableSet.copyOf(JewelryBlocks.JEWELERS_KIT.block().getStateManager().getStates());
    }

    public static VillagerProfession createProfession(String name, RegistryKey<PointOfInterestType> workStation) {
        var id = new Identifier(JewelryMod.ID, name);
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

    private static VillagerProfession jewelerProfession;

    /// Builds the jeweler profession once, keyed by {@link #JEWELER_ID}. Creation only — nothing is
    /// registered here, so a loader that registers the profession itself hands this to its own
    /// registration API instead of duplicating the construction.
    public static VillagerProfession jewelerProfessionToRegister() {
        if (jewelerProfession == null) {
            var workStation = RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), POI_ID);
            jewelerProfession = createProfession(JEWELER, workStation);
        }
        return jewelerProfession;
    }

    /// Reads {@link #JEWELER_PROFESSION} back out of the registry, for a loader that registered the
    /// profession itself. `VillagerTradesEvent` filtering and the Fabric trade registration both compare
    /// against this field, and Forge's `RegisterEvent` helper returns void, so Forge calls this straight
    /// after its registration loop. Throws if the profession is missing — which is also what catches a
    /// silently mis-keyed `event.register` block.
    public static void linkProfessionEntry() {
        if (JEWELER_PROFESSION == null) {
            JEWELER_PROFESSION = Registries.VILLAGER_PROFESSION
                    .getOrEmpty(JEWELER_ID)
                    .orElseThrow(() -> new IllegalStateException(
                            "Villager profession " + JEWELER_ID + " is not in the registry — register it first"));
        }
    }

    public static void registerVillagers() {
        // Register the profession only; trade-offer registration is loader-specific and lives in each
        // platform's entrypoint (Fabric `TradeOfferHelper` / Forge `VillagerTradesEvent`), consuming
        // the shared #createTrades() map.
        JEWELER_PROFESSION = Registry.register(Registries.VILLAGER_PROFESSION, JEWELER_ID, jewelerProfessionToRegister());
    }

    /// 1.20.1's `TradeOffers` has no `BuyItemFactory` at all, and `SellItemFactory` -- while public in
    /// the decompiled tree -- is a *package-private* class in the real 1.20.1 jar (sibling ports reach it
    /// only because another dependency's access widener happens to open it). Both helpers therefore build
    /// the offer on the raw `TradeOffer` constructor, reproducing vanilla's factories exactly, including
    /// the 0.05 price multiplier.
    private static TradeOffers.Factory buy(ItemConvertible item, int count, int maxUses, int experience, int price) {
        return (Entity entity, Random random) -> new TradeOffer(
                new ItemStack(item, count), new ItemStack(Items.EMERALD, price), maxUses, experience, 0.05F);
    }

    /// Mirrors `TradeOffers.SellItemFactory(Item, price, count, maxUses, experience)`.
    private static TradeOffers.Factory sell(ItemConvertible item, int price, int count, int maxUses, int experience) {
        return (Entity entity, Random random) -> new TradeOffer(
                new ItemStack(Items.EMERALD, price), new ItemStack(item, count), maxUses, experience, 0.05F);
    }

    public static LinkedHashMap<Integer, List<TradeOffers.Factory>> createTrades() {
        LinkedHashMap<Integer, List<TradeOffers.Factory>> trades = new LinkedHashMap<>();

        trades.put(1, List.of(
                buy(Items.COPPER_INGOT, 8, 8, 3, 2),
                buy(Items.STRING, 7, 6, 3, 2),
                sell(JewelryItems.copper_ring.item(), 4, 1, 12, 4)
        ));
        trades.put(2, List.of(
                buy(Items.GOLD_INGOT, 7, 8, 2, 8),
                sell(JewelryItems.iron_ring.item(), 4, 1, 6, 5),
                sell(JewelryItems.gold_ring.item(), 18, 1, 6, 5)
        ));
        trades.put(3, List.of(
                buy(Items.DIAMOND, 1, 12, 10, 10),
                sell(JewelryItems.emerald_necklace.item(), 20, 1, 12, 10),
                sell(JewelryItems.diamond_necklace.item(), 25, 1, 12, 10)
        ));
        trades.put(4, List.of(
                sell(JewelryItems.ruby_ring.item(), 35, 1, 5, 15),
                sell(JewelryItems.topaz_ring.item(), 35, 1, 5, 15),
                sell(JewelryItems.citrine_ring.item(), 35, 1, 5, 15),
                sell(JewelryItems.jade_ring.item(), 35, 1, 5, 15),
                sell(JewelryItems.sapphire_ring.item(), 35, 1, 5, 13),
                sell(JewelryItems.tanzanite_ring.item(), 35, 1, 5, 13)
        ));
        trades.put(5, List.of(
                sell(JewelryItems.ruby_necklace.item(), 45, 1, 3, 15),
                sell(JewelryItems.topaz_necklace.item(), 45, 1, 3, 15),
                sell(JewelryItems.citrine_necklace.item(), 45, 1, 3, 15),
                sell(JewelryItems.jade_necklace.item(), 45, 1, 3, 15),
                sell(JewelryItems.sapphire_necklace.item(), 45, 1, 3, 15),
                sell(JewelryItems.tanzanite_necklace.item(), 45, 1, 3, 15)
        ));

        return trades;
    }
}
