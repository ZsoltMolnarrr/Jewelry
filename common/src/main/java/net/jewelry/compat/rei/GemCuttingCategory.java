package net.jewelry.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.jewelry.blocks.JewelryBlocks;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/** Laid out like REI's own stonecutting category, the closest vanilla analogue: raw gem → cut gem. */
// No `@Environment`: Architectury turns it into `@OnlyIn`, which NeoForge 21.11+ warns about at load.
// Client-only all the same: only `JewelryReiClientPlugin` references it.
public class GemCuttingCategory implements DisplayCategory<GemCuttingDisplay> {
    @Override
    public CategoryIdentifier<? extends GemCuttingDisplay> getCategoryIdentifier() {
        return GemCuttingDisplay.CATEGORY;
    }

    /** Reuses the Jeweler's Kit block name (the cutting screen's own title), so no new lang key needs translating. */
    @Override
    public Component getTitle() {
        return Component.translatable(JewelryBlocks.JEWELERS_KIT.block().getDescriptionId());
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(JewelryBlocks.JEWELERS_KIT.item());
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }

    @Override
    public List<Widget> setupDisplay(GemCuttingDisplay display, Rectangle bounds) {
        // raw gem [->] cut gem, centred in the panel
        Point start = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 9);
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createSlot(new Point(start.x + 4, start.y)).entries(display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createArrow(new Point(start.x + 27, start.y)));
        widgets.add(Widgets.createResultSlotBackground(new Point(start.x + 61, start.y)));
        widgets.add(Widgets.createSlot(new Point(start.x + 61, start.y))
                .entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        return widgets;
    }
}
