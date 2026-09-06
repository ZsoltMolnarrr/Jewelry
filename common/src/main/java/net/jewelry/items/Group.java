package net.jewelry.items;

import net.jewelry.JewelryMod;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Group {
    public static Identifier ID = new Identifier(JewelryMod.ID, "generic");
    public static RegistryKey<ItemGroup> KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), ID);
    // Vanilla ItemGroup.Builder — FabricItemGroup.builder() is Fabric-API-only and this static
    // initializer runs on both loaders (the group is created in common, registered from registerItems).
    public static ItemGroup JEWELRY = new ItemGroup.Builder(ItemGroup.Row.TOP, 0)
            // 1.20.1 `Registry` has no `getEntry(Identifier)`; the plain lookup is enough here, and it
            // runs lazily (the icon supplier is only called once the group is displayed).
            .icon(() -> new ItemStack(Registries.ITEM.get(JewelryItems.ruby_ring.id())))
            // `.generic` suffix is required by older versions, keeping it for translation consistency
            .displayName(Text.translatable("itemGroup." + JewelryMod.ID + ".generic"))
            .build();
}
