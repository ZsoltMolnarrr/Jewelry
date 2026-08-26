package net.jewelry.neoforge.compat.curios;

import net.jewelry.items.JewelryItem;
import net.jewelry.util.SoundHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.function.Consumer;

public class JewelryCurioItem extends Item implements ICurioItem, JewelryItem {
    private final String lore;

    public JewelryCurioItem(Item.Properties settings, String lore) {
        super(settings);
        this.lore = lore;
    }

    // 1.21.6+: `appendTooltip` takes a `TooltipDisplayComponent` and a `Consumer<Text>` sink.
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent,
                              Consumer<Component> textConsumer, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
        if (lore != null && !lore.isEmpty()) {
            textConsumer.accept(Component.translatable(lore).withStyle(ChatFormatting.ITALIC, ChatFormatting.GOLD));
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
        var world = entity.level();
        if (world.isClientSide()                        // the server broadcast below reaches every nearby client
                || entity.tickCount <= 100                // gear already worn when entering a world/dimension
                || prevStack.is(stack.getItem())) // same item, only its data changed
        {
            return;
        }
        world.playSound(null, entity.blockPosition(), SoundHelper.JEWELRY_EQUIP, entity.getSoundSource(), 1.0F, 1.0F);
    }

    @Override
    public void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
        // Silent on purpose. `onEquip` above already fires for every equip path, this one included.
        // Curios' default would route through `ICurio`'s stateless instance, which resolves
        // `getEquipSound` on that instance rather than on this item, playing a generic sound.
    }
}
