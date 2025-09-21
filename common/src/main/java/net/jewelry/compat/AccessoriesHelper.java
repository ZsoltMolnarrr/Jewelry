package net.jewelry.compat;

import io.wispforest.accessories.api.components.AccessoriesDataComponents;
import io.wispforest.accessories.api.components.AccessoryItemAttributeModifiers;
import net.jewelry.items.JewelryFactory;
import net.jewelry.util.SoundHelper;

public class AccessoriesHelper {
    public static void registerFactory() {
        JewelryFactory.factory = args -> {
            var settings = args.settings();
            var attributes = args.attributes();
            var slot = args.slot() != null ? args.slot() : "ring"; // Use provided slot or default

            if (attributes != null) {
                var builder = AccessoryItemAttributeModifiers.builder();
                for (var bonus : attributes.modifiers()) {
                    builder = builder.addForSlot(bonus.attribute(), bonus.modifier(), slot, true);
                }
                settings = settings.component(AccessoriesDataComponents.ATTRIBUTES, builder.build());
            }

            return new JewelryAccessoriesItem(
                    settings,
                    args.lore(),
                    () -> SoundHelper.JEWELRY_EQUIP_ENTRY);
        };
    }
}
