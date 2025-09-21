package net.jewelry.compat;

import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.SoundEventData;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.Supplier;

public class JewelryAccessoriesItem extends AccessoryItem {
    private final Supplier<RegistryEntry<SoundEvent>> equipSound;
    public JewelryAccessoriesItem(Settings properties, String lore, Supplier<RegistryEntry<SoundEvent>> equipSound) {
        super(properties);
        this.lore = lore;
        this.equipSound = equipSound;
    }

    private final String lore;
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (lore != null && !lore.isEmpty()) {
            tooltip.add(Text.translatable(lore).formatted(Formatting.ITALIC, Formatting.GOLD));
        }
    }

    public SoundEventData getEquipSound(ItemStack stack, SlotReference reference) {
        var entry = this.equipSound.get();
        if (entry != null) {
            return new SoundEventData(entry, 1.0F, 1.0F);
        } else {
            return null;
        }
    }
}
