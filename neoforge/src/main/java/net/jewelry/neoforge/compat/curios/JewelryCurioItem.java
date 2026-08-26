package net.jewelry.neoforge.compat.curios;

import net.jewelry.items.JewelryItem;
import net.jewelry.util.SoundHelper;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.function.Consumer;

public class JewelryCurioItem extends Item implements ICurioItem, JewelryItem {
    private final String lore;

    public JewelryCurioItem(Item.Settings settings, String lore) {
        super(settings);
        this.lore = lore;
    }

    // 1.21.6+: `appendTooltip` takes a `TooltipDisplayComponent` and a `Consumer<Text>` sink.
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent,
                              Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
        if (lore != null && !lore.isEmpty()) {
            textConsumer.accept(Text.translatable(lore).formatted(Formatting.ITALIC, Formatting.GOLD));
        }
    }

    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundHelper.JEWELRY_EQUIP, 1.0F, 1.0F);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        var entity = slotContext.entity();
        if (entity == null) {
            return;
        }
        var world = entity.getEntityWorld();
        if (world.isClient()                        // the server broadcast below reaches every nearby client
                || entity.age <= 100                // gear already worn when entering a world/dimension
                || prevStack.isOf(stack.getItem())) // same item, only its data changed
        {
            return;
        }
        world.playSound(null, entity.getBlockPos(), SoundHelper.JEWELRY_EQUIP, entity.getSoundCategory(), 1.0F, 1.0F);
    }

    @Override
    public void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
        // Silent on purpose. `onEquip` above already fires for every equip path, this one included.
        // Curios' default would route through `ICurio`'s stateless instance, which resolves
        // `getEquipSound` on that instance rather than on this item, playing a generic sound.
    }
}
