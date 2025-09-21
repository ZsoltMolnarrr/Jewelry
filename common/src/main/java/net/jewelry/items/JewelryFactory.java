package net.jewelry.items;

import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class JewelryFactory {
    public record ItemArgs(Item.Settings settings, @Nullable AttributeModifiersComponent attributes, @Nullable String lore, @Nullable String slot) { }

    public static Function<ItemArgs, Item> factory = args -> {
        var settings = args.settings;
        if (args.attributes != null) {
            settings.attributeModifiers(args.attributes);
        }
        return new VanillaJewelryItem(settings, args.lore);
    };

    public static Function<ItemArgs, Item> getFactory() {
        return factory;
    }
}