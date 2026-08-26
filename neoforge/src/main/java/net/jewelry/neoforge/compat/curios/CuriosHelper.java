package net.jewelry.neoforge.compat.curios;

import net.jewelry.items.JewelryFactory;
import net.minecraft.component.type.AttributeModifiersComponent;
import top.theillusivec4.curios.api.CurioAttributeModifiers;
import top.theillusivec4.curios.api.CuriosDataComponents;

public class CuriosHelper {
    public static void registerFactory() {
        JewelryFactory.factory = args -> {
            var settings = args.settings();
            var attributes = args.attributes();
            // Curios 14 applies bonuses from its own `curios:attribute_modifiers` component only.
            // It never reads the vanilla `minecraft:attribute_modifiers` component (that one would
            // also apply while the ring is merely held), and the `ICurioItem#getAttributeModifiers`
            // override is deprecated and no longer called on the equip path.
            if (attributes != null && !attributes.modifiers().isEmpty()) {
                settings = settings.component(CuriosDataComponents.ATTRIBUTE_MODIFIERS, curioModifiers(attributes));
            }
            return new JewelryCurioItem(settings, args.lore());
        };
    }

    /**
     * Converts the config-built component into Curios' one. Modifier ids stay per-item, per-attribute;
     * Curios itself suffixes them with the slot id + index on equip, so bonuses stack across slots.
     */
    private static CurioAttributeModifiers curioModifiers(AttributeModifiersComponent attributes) {
        var builder = CurioAttributeModifiers.builder();
        for (var entry : attributes.modifiers()) {
            builder.addModifier(entry.attribute(), entry.modifier());
        }
        return builder.build();
    }
}
