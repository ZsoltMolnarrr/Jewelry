package net.jewelry.fabric.compat.trinkets;

import net.jewelry.items.JewelryFactory;

public class TrinketsHelper {
    public static void registerFactory() {
        JewelryFactory.factory = args -> {
            var attributes = args.attributes();
            var item = new JewelryTrinketItem(args.settings(), args.lore());
            // Passed here instead of through Item.Settings, because Trinkets applies its own
            // per-slot modifiers (`Trinket#getModifiers`) and ignores the vanilla item modifiers.
            item.setConfigurableModifiers(attributes);
            return item;
        };
    }
}
