package net.jewelry.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.jewelry.gems.GemCut;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Display of a single gem cut: raw gem → cut gem.
 * Laid out exactly like EMI's own stonecutting recipe, which is the closest vanilla analogue.
 */
@Environment(EnvType.CLIENT)
public class GemCuttingEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final EmiIngredient input;
    private final EmiStack output;

    public GemCuttingEmiRecipe(Identifier id, RegistryEntry<GemCut> cut) {
        this.id = id;
        this.input = EmiStack.of(cut.value().gem().value());
        this.output = EmiStack.of(GemCut.stack(cut));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return JewelryEmiPlugin.CATEGORY;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 76;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 26, 1);
        widgets.addSlot(input, 0, 0);
        widgets.addSlot(output, 58, 0).recipeContext(this);
    }
}
