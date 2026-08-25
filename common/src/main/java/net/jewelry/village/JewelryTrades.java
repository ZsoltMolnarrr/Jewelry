package net.jewelry.village;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;

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
            implements TradeOffers.Factory {
        public Sell(Item item, int price, int count, int maxUses, int experience) {
            this(item, price, count, maxUses, experience, 0.05F);
        }

        @Override
        public TradeOffer create(ServerWorld world, Entity entity, Random random) {
            var sold = new ItemStack(item);
            sold.setCount(count);
            return new TradeOffer(new TradedItem(Items.EMERALD, price), sold, maxUses, experience, multiplier);
        }
    }

    /// Villager buys `count` x `item` for `price` emeralds.
    /// Mirrors vanilla `TradeOffers.BuyItemFactory(item, count, maxUses, experience, price)`.
    public record Buy(ItemConvertible item, int count, int maxUses, int experience, int price)
            implements TradeOffers.Factory {
        @Override
        public TradeOffer create(ServerWorld world, Entity entity, Random random) {
            return new TradeOffer(new TradedItem(item.asItem(), count),
                    new ItemStack(Items.EMERALD, price), maxUses, experience, 0.05F);
        }
    }
}
