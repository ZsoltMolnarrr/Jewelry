package net.jewelry.fabric.compat.trinkets;

import net.jewelry.items.JewelryFactory;

public class TrinketsHelper {
    public static void registerFactory() {
        JewelryFactory.factory = args -> {
            var attributes = args.attributes();
            var item = new JewelryTrinketItem(args.settings(), args.lore());
            // Passing attributes here instead Item.Settings, because Trinkets ignores `AttributeModifiersComponent`
            if (attributes != null) {
                item.setConfigurableModifiers(attributes);
            }
            return item;
        };
    }
}
