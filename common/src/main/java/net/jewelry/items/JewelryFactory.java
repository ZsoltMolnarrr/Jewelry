package net.jewelry.items;

import org.jspecify.annotations.Nullable;

import java.util.function.Function;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class JewelryFactory {
    public record ItemArgs(Item.Properties settings, @Nullable ItemAttributeModifiers attributes, @Nullable String lore, @Nullable String slot) { }

    public static Function<ItemArgs, Item> factory = args -> {
        var settings = args.settings;
        if (args.attributes != null) {
            settings.attributes(args.attributes);
        }
        return new VanillaJewelryItem(settings, args.lore);
    };

    public static Function<ItemArgs, Item> getFactory() {
        return factory;
    }
}