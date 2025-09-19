package net.jewelry.fabric.compat.trinkets;

import net.jewelry.items.Factory;

public class TrinketsHelper {
    public static void registerFactory() {
        Factory.Holder.factory = (settings, attributes, lore, slot) -> {
            var item = new JewelryTrinketItem(settings, lore);
            // Passing attriubtes here instead Item.Settings, because Trinkets ignores `AttributeModifiersComponent`
            item.setConfigurableModifiers(attributes);
            return item;
        };
    }
}
