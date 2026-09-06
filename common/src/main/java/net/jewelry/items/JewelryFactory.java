package net.jewelry.items;

import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class JewelryFactory {
    public record ItemArgs(Item.Settings settings, JewelryModifiers attributes, @Nullable String lore, @Nullable String slot) { }

    public static Function<ItemArgs, Item> factory = args ->
            new VanillaJewelryItem(args.settings(), args.attributes(), args.lore());

    public static Function<ItemArgs, Item> getFactory() {
        return factory;
    }
}
