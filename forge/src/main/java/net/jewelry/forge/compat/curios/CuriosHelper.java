package net.jewelry.forge.compat.curios;

import net.jewelry.items.JewelryFactory;

public class CuriosHelper {
    public static void registerFactory() {
        JewelryFactory.factory = args -> {
            var attributes = args.attributes();
            var item = new JewelryCurioItem(args.settings(), args.lore());
            // Passed here instead of through Item.Settings, because Curios applies its own
            // per-slot modifiers (`ICurioItem#getAttributeModifiers`) and ignores the vanilla ones.
            item.setConfigurableModifiers(attributes);
            return item;
        };
    }
}
