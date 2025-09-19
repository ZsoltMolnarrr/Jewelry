package net.jewelry.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.jewelry.items.Factory;
import net.jewelry.util.SoundHelper;

public class AccessoriesCompat {
    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("accessories")) {
            Factory.Holder.factory = (settings, attributes, lore) -> new JewelryAccessoriesItem(
                    settings.attributeModifiers(attributes),
                    lore,
                    () -> SoundHelper.JEWELRY_EQUIP_ENTRY);
        }
    }
}
