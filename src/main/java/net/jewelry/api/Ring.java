package net.jewelry.api;

import net.jewelry.config.ItemsConfig;
import net.minecraft.item.Item;

public class Ring {
    public record Entry(String namespace, String name, Item item, ItemsConfig.Item config) { }
}
