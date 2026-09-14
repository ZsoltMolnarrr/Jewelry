package net.jewelry.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeSorting;
import dev.emi.emi.api.stack.EmiStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.gems.GemCut;
import net.jewelry.gems.GemCutRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

/**
 * Surfaces gem cuts — entries of the synced {@code gem_cut} datapack registry, which are not
 * recipes at all as far as vanilla (and therefore EMI) is concerned — under their own category,
 * worked at the Jeweler's Kit.
 * <p>
 * Loaded reflectively by EMI only: Fabric via the {@code emi} entrypoint in {@code fabric.mod.json},
 * NeoForge via the {@link EmiEntrypoint} annotation scan. Nothing in Jewelry references this class,
 * so it is never class-loaded when EMI is absent.
 */
@EmiEntrypoint
@Environment(EnvType.CLIENT)
public class JewelryEmiPlugin implements EmiPlugin {
    /** Category id {@code jewelry:gem_cutting} → name key {@code emi.category.jewelry.gem_cutting}. */
    public static final Identifier CATEGORY_ID = Identifier.of(JewelryMod.ID, "gem_cutting");
    public static final EmiStack JEWELERS_KIT = EmiStack.of(JewelryBlocks.JEWELERS_KIT.item());
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(
            CATEGORY_ID, JEWELERS_KIT, JEWELERS_KIT, EmiRecipeSorting.compareInputThenOutput());

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(CATEGORY);
        registry.addWorkstation(CATEGORY, JEWELERS_KIT);

        // The gem cut registry is datapack-driven and synced, so it is reachable only through the
        // client world's registry manager. EMI refuses to run plugins while `client.world` is null,
        // so this is populated by the time we get here — the guard is belt and braces.
        var world = MinecraftClient.getInstance().world;
        if (world == null) {
            return;
        }
        // Iterating the registry (rather than Jewelry's own gem list) picks up cuts contributed by
        // other mods and data packs, including cuts for gems Jewelry does not own.
        GemCutRegistry.from(world).streamEntries().forEach(entry -> {
            RegistryEntry<GemCut> cut = entry;
            GemCut.idOf(cut).ifPresent(cutId -> registry.addRecipe(new GemCuttingEmiRecipe(recipeId(cutId), cut)));
        });
    }

    /**
     * {@code jewelry:bold_ruby} → {@code jewelry:gem_cutting/bold_ruby}. EMI recipe ids share one
     * namespace with every real recipe id, so the category path segment keeps these synthetic
     * entries from colliding with a same-named recipe.
     */
    public static Identifier recipeId(Identifier cutId) {
        return Identifier.of(cutId.getNamespace(), "gem_cutting/" + cutId.getPath());
    }
}
