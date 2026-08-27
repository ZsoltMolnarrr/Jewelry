package net.jewelry.village;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.util.SoundHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Set;

public class JewelryVillagers {
    public static final String JEWELER = "jeweler";

    // These will be set by platform-specific code
    public static VillagerProfession JEWELER_PROFESSION;
    public static Identifier POI_ID = Identifier.fromNamespaceAndPath(JewelryMod.ID, JEWELER);
    public static final int POI_TICKET_COUNT = 1;
    public static final int POI_SEARCH_DISTANCE = 10;

    /// Registry key of {@link #JEWELER_PROFESSION} — since 1.21.2 professions are addressed by key.
    public static final ResourceKey<VillagerProfession> JEWELER_PROFESSION_KEY =
            ResourceKey.create(Registries.VILLAGER_PROFESSION, Identifier.fromNamespaceAndPath(JewelryMod.ID, JEWELER));

    /// The jeweler's-kit workstation block states for the POI. Registration itself is loader-specific
    /// (Fabric: `PoiHelper`; NeoForge: a plain `Registry.register` of a `PointOfInterestType`)
    /// and lives in each platform's entrypoint; this only exposes the shared state set.
    public static Set<BlockState> poiBlockStates() {
        return ImmutableSet.copyOf(JewelryBlocks.JEWELERS_KIT.block().getStateDefinition().getPossibleStates());
    }

    /// 26.1 made villager trades data driven: the offers live in
    /// `data/jewelry/villager_trade/jeweler/<level>/*.json`, are grouped by
    /// `data/jewelry/tags/villager_trade/jeweler/level_<n>.json` and picked up by
    /// `data/jewelry/trade_set/jeweler/level_<n>.json`. The profession only names the trade-set key
    /// per merchant level — `VillagerTrades.ItemListing`, Fabric's `TradeOfferHelper` and NeoForge's
    /// `VillagerTradesEvent` are all gone.
    public static ResourceKey<TradeSet> tradeSet(int level) {
        return ResourceKey.create(Registries.TRADE_SET,
                Identifier.fromNamespaceAndPath(JewelryMod.ID, JEWELER + "/level_" + level));
    }

    public static Int2ObjectMap<ResourceKey<TradeSet>> tradeSetsByLevel() {
        return Int2ObjectMap.ofEntries(
                Int2ObjectMap.entry(1, tradeSet(1)),
                Int2ObjectMap.entry(2, tradeSet(2)),
                Int2ObjectMap.entry(3, tradeSet(3)),
                Int2ObjectMap.entry(4, tradeSet(4)),
                Int2ObjectMap.entry(5, tradeSet(5))
        );
    }

    public static VillagerProfession createProfession(String name, ResourceKey<PoiType> workStation) {
        var id = Identifier.fromNamespaceAndPath(JewelryMod.ID, name);
        return new VillagerProfession(
                // 1.21.11: the record's first component is the displayed name as a `Text`, not the id
                // string vanilla used to build `entity.minecraft.villager.<id>` from. Pass the key the
                // existing translation files already carry: `entity.minecraft.villager.jewelry.jeweler`.
                Component.translatable("entity.minecraft.villager." + id.getNamespace() + "." + id.getPath()),
                (entry) -> {
                    return entry.is(workStation);
                },
                (entry) -> {
                    return entry.is(workStation);
                },
                ImmutableSet.of(),
                ImmutableSet.of(),
                SoundHelper.JEWELRY_WORKBENCH,
                tradeSetsByLevel()
        );
    }

    public static void registerVillagers() {
        var workStation = ResourceKey.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE.key(), POI_ID);
        JEWELER_PROFESSION = Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, Identifier.fromNamespaceAndPath(JewelryMod.ID, JEWELER), createProfession(JEWELER, workStation));
    }
}
