package net.jewelry.village;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;

/// Trade-offer factories for the jeweler.
///
/// Vanilla's `TradeOffers.SellItemFactory` / `BuyItemFactory` became package-private in 1.21.11
/// (Fabric API widens them transitively, NeoForge does not), so common code can no longer reference
/// them on both loaders — it would compile and then throw on NeoForge. These are behaviour-identical
/// re-implementations over the public [TradeOffer] / [TradedItem] API.
public final class JewelryTrades {
    private JewelryTrades() { }

    /// Villager sells `count` x `item` for `price` emeralds.
    /// Mirrors vanilla `TradeOffers.SellItemFactory(item, price, count, maxUses, experience)`.
    public record Sell(Item item, int price, int count, int maxUses, int experience, float multiplier)
            implements VillagerTrades.ItemListing {
        public Sell(Item item, int price, int count, int maxUses, int experience) {
            this(item, price, count, maxUses, experience, 0.05F);
        }

        @Override
        public MerchantOffer getOffer(ServerLevel world, Entity entity, RandomSource random) {
            var sold = new ItemStack(item);
            sold.setCount(count);
            return new MerchantOffer(new ItemCost(Items.EMERALD, price), sold, maxUses, experience, multiplier);
        }
    }

    /// Villager buys `count` x `item` for `price` emeralds.
    /// Mirrors vanilla `TradeOffers.BuyItemFactory(item, count, maxUses, experience, price)`.
    public record Buy(ItemLike item, int count, int maxUses, int experience, int price)
            implements VillagerTrades.ItemListing {
        @Override
        public MerchantOffer getOffer(ServerLevel world, Entity entity, RandomSource random) {
            return new MerchantOffer(new ItemCost(item.asItem(), count),
                    new ItemStack(Items.EMERALD, price), maxUses, experience, 0.05F);
        }
    }
}
