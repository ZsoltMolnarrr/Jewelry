package net.jewelry.items;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
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
    public static ItemGroup JEWELRY = FabricItemGroup.builder()
            .icon(() -> new ItemStack(JewelryItems.red_ring.item()))
            .displayName(Text.translatable("itemGroup." + JewelryMod.ID))
            .build();
}
