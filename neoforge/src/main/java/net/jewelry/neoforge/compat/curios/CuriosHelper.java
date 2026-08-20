package net.jewelry.neoforge.compat.curios;

import net.jewelry.items.JewelryFactory;

public class CuriosHelper {
    public static void registerFactory() {
        JewelryFactory.factory = args -> {
            var attributes = args.attributes();
            var item = new JewelryCurioItem(args.settings(), args.lore());
            // Passing attributes here instead of Item.Settings, because Curios ignores `AttributeModifiersComponent`
            if (attributes != null) {
                item.setConfigurableModifiers(attributes);
            }
            return item;
        };
    }
}
