package net.jewelry.fabric.compat.trinkets;

import net.fabricmc.loader.api.FabricLoader;
import net.jewelry.items.Factory;
import net.jewelry.items.JewelryItem;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.Item;

public class TrinketsCompat {
    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            Factory.Holder.factory = new Factory() {
                @Override
                public JewelryItem create(Item.Settings settings, AttributeModifiersComponent attributes, String lore) {
                    var item = new JewelryTrinketItem(settings, lore);
                    // Passing attriubtes here instead Item.Settings, because Trinkets ignores `AttributeModifiersComponent`
                    item.setConfigurableModifiers(attributes);
                    return item;
                }
            };
        }
    }
}
