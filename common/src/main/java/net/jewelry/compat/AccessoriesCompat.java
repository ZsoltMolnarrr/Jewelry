package net.jewelry.compat;

import io.wispforest.accessories.api.components.AccessoriesDataComponents;
import io.wispforest.accessories.api.components.AccessoryItemAttributeModifiers;
import net.fabricmc.loader.api.FabricLoader;
import net.jewelry.items.Factory;
import net.jewelry.util.SoundHelper;

public class AccessoriesCompat {
    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("accessories")) {
            Factory.Holder.factory = (settings, attributes, lore, slot) -> {
                var builder = AccessoryItemAttributeModifiers.builder();
                for (var bonus : attributes.modifiers()) {
                    builder = builder.addForSlot(bonus.attribute(), bonus.modifier(), slot, true);
                }
                return new JewelryAccessoriesItem(
                        settings.component(AccessoriesDataComponents.ATTRIBUTES, builder.build()),
                        lore,
                        () -> SoundHelper.JEWELRY_EQUIP_ENTRY);
            };
        }
    }
}
