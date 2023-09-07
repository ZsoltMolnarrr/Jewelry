package net.jewelry.items;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.jewelry.JewelryMod;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Group {
    public static Identifier ID = new Identifier(JewelryMod.ID, "generic");
    public static ItemGroup JEWELRY = FabricItemGroupBuilder.build(ID,
            () -> new ItemStack(JewelryItems.ruby_ring.item()));
}
