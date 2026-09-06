package net.jewelry.client;

import net.jewelry.items.JewelryItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class JewelryModClient {
    /// Removes duplicate tooltip lines on jewelry items (needed where multiple Ring slots exist).
    /// Loader-neutral — each platform's client entrypoint calls this from its own tooltip event
    /// (Fabric `ItemTooltipCallback`; Forge `ItemTooltipEvent`), so `common` needs no Fabric API.
    public static void removeTooltipDuplicates(ItemStack itemStack, List<Text> tooltip) {
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
