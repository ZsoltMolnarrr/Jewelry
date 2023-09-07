package net.jewelry.village;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.items.JewelryItems;
import net.jewelry.util.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.List;

public class JewelryVillagers {
    public static final String JEWELER = "jeweler";

    public static PointOfInterestType registerPOI(String name, Block block) {
        return PointOfInterestHelper.register(new Identifier(JewelryMod.ID, name),
                1, 10, ImmutableSet.copyOf(block.getStateManager().getStates()));
    }

    public static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> workStation) {
        var id = new Identifier(JewelryMod.ID, name);
        return Registry.register(Registries.VILLAGER_PROFESSION, new Identifier(JewelryMod.ID, name), new VillagerProfession(
                id.toString(),
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                ImmutableSet.of(),
                ImmutableSet.of(),
                SoundHelper.JEWELRY_WORKBENCH)
        );
    }

    private static class Offer {
        int level;
        ItemStack input;
        ItemStack output;
        int maxUses;
        int experience;
        float priceMultiplier;

        public Offer(int level, ItemStack input, ItemStack output, int maxUses, int experience, float priceMultiplier) {
            this.level = level;
            this.input = input;
            this.output = output;
            this.maxUses = maxUses;
            this.experience = experience;
            this.priceMultiplier = priceMultiplier;
        }

        public static Offer buy(int level, ItemStack item, int price, int maxUses, int experience, float priceMultiplier) {
            return new Offer(level, item, new ItemStack(Items.EMERALD, price), maxUses, experience, priceMultiplier);
        }

        public static Offer sell(int level, ItemStack item, int price, int maxUses, int experience, float priceMultiplier) {
            return new Offer(level, new ItemStack(Items.EMERALD, price), item, maxUses, experience, priceMultiplier);
        }
    }

    public static void register() {
        var wizardPOI = registerPOI(JEWELER, JewelryBlocks.JEWELERS_KIT.block());
        var wizardMerchantProfession = registerProfession(
                JEWELER,
                RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), new Identifier(JewelryMod.ID, JEWELER)));

        List<Offer> wizardMerchantOffers = List.of(
                Offer.buy(1, new ItemStack(Items.COPPER_INGOT, 8), 4, 8, 1, 0.01f),
                Offer.buy(1, new ItemStack(Items.STRING, 8), 3, 6, 1, 0.01f),
                Offer.sell(1, JewelryItems.copper_ring.item().getDefaultStack(), 4, 12, 2, 0.2f),


                Offer.buy(2, new ItemStack(Items.GOLD_INGOT, 5), 8, 8, 5, 0.05f),
                Offer.sell(2, JewelryItems.iron_ring.item().getDefaultStack(), 4, 4, 4, 0.2f),
                Offer.sell(2, JewelryItems.gold_ring.item().getDefaultStack(), 18, 4, 5, 0.1f),

                // Mediocre necklaces and material buys

                Offer.buy(3, new ItemStack(Items.DIAMOND, 1), 8, 12, 10, 0.05f),
                Offer.sell(3, JewelryItems.emerald_necklace.item().getDefaultStack(), 20, 8, 10, 0.2f),
                Offer.sell(3, JewelryItems.diamond_necklace.item().getDefaultStack(), 25, 8, 10, 0.2f),

                // T1 Rings

                Offer.sell(4, JewelryItems.ruby_ring.item().getDefaultStack(), 35, 5, 13, 0.1f),
                Offer.sell(4, JewelryItems.topaz_ring.item().getDefaultStack(), 35, 5, 13, 0.1f),
                Offer.sell(4, JewelryItems.citrine_ring.item().getDefaultStack(), 35, 5, 13, 0.1f),
                Offer.sell(4, JewelryItems.jade_ring.item().getDefaultStack(), 35, 5, 13, 0.1f),
                Offer.sell(4, JewelryItems.sapphire_ring.item().getDefaultStack(), 35, 5, 13, 0.1f),
                Offer.sell(4, JewelryItems.tanzanite_ring.item().getDefaultStack(), 35, 5, 13, 0.1f),

                // T1 Necklaces

                Offer.sell(5, JewelryItems.ruby_necklace.item().getDefaultStack(), 45, 3, 13, 0.1f),
                Offer.sell(5, JewelryItems.topaz_necklace.item().getDefaultStack(), 45, 3, 13, 0.1f),
                Offer.sell(5, JewelryItems.citrine_necklace.item().getDefaultStack(), 45, 3, 13, 0.1f),
                Offer.sell(5, JewelryItems.jade_necklace.item().getDefaultStack(), 45, 3, 13, 0.1f),
                Offer.sell(5, JewelryItems.sapphire_necklace.item().getDefaultStack(), 45, 3, 13, 0.1f),
                Offer.sell(5, JewelryItems.tanzanite_necklace.item().getDefaultStack(), 45, 3, 13, 0.1f)
        );

        for(var offer: wizardMerchantOffers) {
            TradeOfferHelper.registerVillagerOffers(wizardMerchantProfession, offer.level, factories -> {
                factories.add(((entity, random) -> new TradeOffer(
                        offer.input,
                        offer.output,
                        offer.maxUses, offer.experience, offer.priceMultiplier)
                ));
            });
        }
    }
}
