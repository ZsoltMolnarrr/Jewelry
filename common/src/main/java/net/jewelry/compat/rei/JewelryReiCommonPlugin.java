package net.jewelry.compat.rei;

import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.entry.comparison.EntryComparator;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import net.jewelry.JewelryMod;
import net.jewelry.gems.GemItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

/**
 * Side-independent REI registrations for gem cutting.
 * <p>
 * Loaded reflectively by REI only: Fabric via the {@code rei_common} entrypoint in {@code fabric.mod.json},
 * NeoForge via the {@code @REIPluginCommon} subclass in the neoforge module. Nothing in Jewelry references
 * this class, so it is never class-loaded when REI is absent.
 */
public class JewelryReiCommonPlugin implements REICommonPlugin {
    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(GemCuttingDisplay.CATEGORY.getIdentifier(), GemCuttingDisplay.SERIALIZER);
    }

    /**
     * A raw gem and its cuts are one item whose variants differ only in the {@code jewelry:cut} component;
     * without a comparator REI would collapse every variant into one entry, and looking up a cut gem would
     * list the recipes of all of them. Every {@link GemItem} compares by components — other mods' gems too.
     */
    @Override
    public void registerItemComparators(ItemComparatorRegistry registry) {
        var gems = BuiltInRegistries.ITEM.stream().filter(item -> item instanceof GemItem).toArray(Item[]::new);
        if (gems.length > 0) {
            registry.register(EntryComparator.itemComponents(), gems);
        }
    }
}
