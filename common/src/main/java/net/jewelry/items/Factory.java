package net.jewelry.items;

import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.Item;

public interface Factory {
    JewelryItem create(Item.Settings settings, AttributeModifiersComponent attributes, String lore);

    public static class Holder {
        public static Factory factory = new Factory() {
            @Override
            public JewelryItem create(Item.Settings settings, AttributeModifiersComponent attributes, String lore) {
                return new VanillaJewelryItem(settings.attributeModifiers(attributes));
            }
        };
    }
}
