package net.jewelry.fabric.compat.trinkets;

import eu.pb4.trinkets.api.component.TrinketDataComponents;
import eu.pb4.trinkets.api.component.TrinketsAttributeModifiersComponent;
import net.jewelry.items.JewelryFactory;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class TrinketsHelper {
    public static void registerFactory() {
        JewelryFactory.factory = args -> {
            var settings = args.settings();
            var attributes = args.attributes();
            // Trinkets Updated 4.0 applies bonuses from its own `trinkets:attribute_modifiers`
            // component. It never reads the vanilla `minecraft:attribute_modifiers` component
            // (that one would also apply while the ring is merely held), and `TrinketItem`'s
            // overridable `getModifiers` no longer exists.
            if (attributes != null && !attributes.modifiers().isEmpty()) {
                settings = settings.component(TrinketDataComponents.ATTRIBUTE_MODIFIERS, trinketModifiers(attributes));
            }
            return new JewelryTrinketItem(settings, args.lore());
        };
    }

    /// Converts the config-built component into Trinkets'. Modifier ids stay per-item, per-attribute;
    /// entries are `unique` by default, so Trinkets suffixes the id with the equipped slot's path
    /// (`<group>/<slot>/<index>`) and bonuses stack across slots instead of overwriting each other.
    private static TrinketsAttributeModifiersComponent trinketModifiers(ItemAttributeModifiers attributes) {
        var builder = TrinketsAttributeModifiersComponent.builder();
        for (var entry : attributes.modifiers()) {
            builder.add(entry.attribute(), entry.modifier());
        }
        return builder.build();
    }
}
