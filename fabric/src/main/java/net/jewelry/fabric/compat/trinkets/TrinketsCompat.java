package net.jewelry.fabric.compat.trinkets;

import net.fabricmc.loader.api.FabricLoader;
import net.jewelry.items.Factory;

public class TrinketsCompat {
    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            Factory.Holder.factory = (settings, attributes, lore, slot) -> {
                var item = new JewelryTrinketItem(settings, lore);
                // Passing attriubtes here instead Item.Settings, because Trinkets ignores `AttributeModifiersComponent`
                item.setConfigurableModifiers(attributes);
                return item;
            };
        }
    }
}
