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
    public static Identifier ID = Identifier.of(JewelryMod.ID, "generic");
    public static RegistryKey<ItemGroup> KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), ID);
    public static ItemGroup JEWELRY = FabricItemGroup.builder()
            .icon(() -> {
                var item = Registries.ITEM.getEntry(JewelryItems.ruby_ring.id()).get().value();
                return new ItemStack(item);
            })
            // `.generic` suffix is required by older versions, keeping it for translation consistency
            .displayName(Text.translatable("itemGroup." + JewelryMod.ID + ".generic"))
            .build();
}
