package net.jewelry.client;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.jewelry.items.JewelryItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class JewelryModClient {
    /**
     * Runs the mod initializer on the client environment.
     */
    public static void init() {
        // Necessary for installations where multiple Ring slots exist
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, lines) -> {
            removeTooltipDuplicates(itemStack, lines);
        });
    }

    private static void removeTooltipDuplicates(ItemStack itemStack, List<Text> tooltip) {
        if (itemStack.getItem() instanceof JewelryItem) {
            for (int i = 0; i < tooltip.size(); i++) {
                var text = tooltip.get(i);
                for (int j = i + 1; j < tooltip.size(); j++) {
                    if (text.getString().equals(tooltip.get(j).getString())) {
                        tooltip.remove(j);
                        j--;
                    }
                }
            }
        }
    }
}
