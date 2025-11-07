package net.jewelry.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.items.Gems;
import net.jewelry.items.JewelryItems;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class JewelryCraftingRecipes extends FabricRecipeProvider {
    public JewelryCraftingRecipes(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "Jewelry Crafting Recipes";
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateBasicRings(exporter);
        generateGemJewelry(exporter);
        generateGemRings(exporter);
        generateGemNecklaces(exporter);
        generateNetheriteRings(exporter);
        generateNetheriteNecklaces(exporter);
        generateSpecialItems(exporter);
    }

    // ========================================
    // BASIC RING RECIPES
    // ========================================

    private void generateBasicRings(RecipeExporter exporter) {
        metalRing(exporter, JewelryItems.copper_ring.item(), Items.COPPER_INGOT);
        metalRing(exporter, JewelryItems.iron_ring.item(), Items.IRON_INGOT);
        metalRing(exporter, JewelryItems.gold_ring.item(), Items.GOLD_INGOT);
    }

    /**
     * Generate a simple metal ring recipe.
     * Pattern: " M " / "M M" / " M "
     */
    private void metalRing(RecipeExporter exporter, Item result, Item metal) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, result)
                .pattern(" M ")
                .pattern("M M")
                .pattern(" M ")
                .input('M', metal)
                .criterion(hasItem(metal), conditionsFromItem(metal))
                .offerTo(exporter);
    }

    // ========================================
    // VANILLA Jewelry
    // ========================================

    private void generateGemJewelry(RecipeExporter exporter) {
        vanillaNecklace(exporter, JewelryItems.diamond_necklace.item(), Items.DIAMOND);
        gemRing(exporter, JewelryItems.diamond_ring.item(), Items.DIAMOND);
        vanillaNecklace(exporter, JewelryItems.emerald_necklace.item(), Items.EMERALD);
    }

    /**
     * Generate a vanilla necklace recipe (diamond, emerald).
     * Pattern: " S " / " M " / " G "
     */
    private void vanillaNecklace(RecipeExporter exporter, Item result, Item gem) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, result)
                .pattern(" S ")
                .pattern(" M ")
                .pattern(" G ")
                .input('S', Items.STRING)
                .input('M', Items.GOLD_INGOT)
                .input('G', gem)
                .criterion(hasItem(gem), conditionsFromItem(gem))
                .offerTo(exporter);
    }

    // ========================================
    // GEM RING RECIPES
    // ========================================

    private void generateGemRings(RecipeExporter exporter) {
        gemRing(exporter, JewelryItems.ruby_ring.item(), Gems.ruby.item());
        gemRing(exporter, JewelryItems.topaz_ring.item(), Gems.topaz.item());
        gemRing(exporter, JewelryItems.citrine_ring.item(), Gems.citrine.item());
        gemRing(exporter, JewelryItems.jade_ring.item(), Gems.jade.item());
        gemRing(exporter, JewelryItems.sapphire_ring.item(), Gems.sapphire.item());
        gemRing(exporter, JewelryItems.tanzanite_ring.item(), Gems.tanzanite.item());
    }

    /**
     * Generate a gem ring recipe.
     * Pattern: " G " / "M M" / " M "
     */
    private void gemRing(RecipeExporter exporter, Item result, Item gem) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, result)
                .pattern(" G ")
                .pattern("M M")
                .pattern(" M ")
                .input('G', gem)
                .input('M', Items.GOLD_INGOT)
                .criterion(hasItem(gem), conditionsFromItem(gem))
                .offerTo(exporter);
    }

    // ========================================
    // GEM NECKLACE RECIPES
    // ========================================

    private void generateGemNecklaces(RecipeExporter exporter) {
        gemNecklace(exporter, JewelryItems.ruby_necklace.item(), Gems.ruby.item());
        gemNecklace(exporter, JewelryItems.topaz_necklace.item(), Gems.topaz.item());
        gemNecklace(exporter, JewelryItems.citrine_necklace.item(), Gems.citrine.item());
        gemNecklace(exporter, JewelryItems.jade_necklace.item(), Gems.jade.item());
        gemNecklace(exporter, JewelryItems.sapphire_necklace.item(), Gems.sapphire.item());
        gemNecklace(exporter, JewelryItems.tanzanite_necklace.item(), Gems.tanzanite.item());
    }

    /**
     * Generate a gem necklace recipe.
     * Pattern: " S " / " M " / " G "
     */
    private void gemNecklace(RecipeExporter exporter, Item result, Item gem) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, result)
                .pattern(" S ")
                .pattern(" M ")
                .pattern(" G ")
                .input('S', Items.STRING)
                .input('M', Items.GOLD_INGOT)
                .input('G', gem)
                .criterion(hasItem(gem), conditionsFromItem(gem))
                .offerTo(exporter);
    }

    // ========================================
    // NETHERITE RING RECIPES
    // ========================================

    private void generateNetheriteRings(RecipeExporter exporter) {
        netheriteRing(exporter, JewelryItems.netherite_ruby_ring.item(), Gems.ruby.item());
        netheriteRing(exporter, JewelryItems.netherite_topaz_ring.item(), Gems.topaz.item());
        netheriteRing(exporter, JewelryItems.netherite_citrine_ring.item(), Gems.citrine.item());
        netheriteRing(exporter, JewelryItems.netherite_jade_ring.item(), Gems.jade.item());
        netheriteRing(exporter, JewelryItems.netherite_sapphire_ring.item(), Gems.sapphire.item());
        netheriteRing(exporter, JewelryItems.netherite_tanzanite_ring.item(), Gems.tanzanite.item());
    }

    /**
     * Generate a netherite ring recipe.
     * Pattern: " G " / "M M" / " N "
     */
    private void netheriteRing(RecipeExporter exporter, Item result, Item gem) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, result)
                .pattern(" G ")
                .pattern("M M")
                .pattern(" N ")
                .input('G', gem)
                .input('M', Items.GOLD_INGOT)
                .input('N', Items.NETHERITE_INGOT)
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(gem))
                .offerTo(exporter);
    }

    // ========================================
    // NETHERITE NECKLACE RECIPES
    // ========================================

    private void generateNetheriteNecklaces(RecipeExporter exporter) {
        netheriteNecklace(exporter, JewelryItems.netherite_ruby_necklace.item(), Gems.ruby.item());
        netheriteNecklace(exporter, JewelryItems.netherite_topaz_necklace.item(), Gems.topaz.item());
        netheriteNecklace(exporter, JewelryItems.netherite_citrine_necklace.item(), Gems.citrine.item());
        netheriteNecklace(exporter, JewelryItems.nsetherite_jade_necklace.item(), Gems.jade.item());
        netheriteNecklace(exporter, JewelryItems.netherite_sapphire_necklace.item(), Gems.sapphire.item());
        netheriteNecklace(exporter, JewelryItems.netherite_tanzanite_necklace.item(), Gems.tanzanite.item());
    }

    /**
     * Generate a netherite necklace recipe.
     * Pattern: " S " / "TMT" / " G "
     */
    private void netheriteNecklace(RecipeExporter exporter, Item result, Item gem) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, result)
                .pattern(" S ")
                .pattern("TMT")
                .pattern(" G ")
                .input('S', Items.STRING)
                .input('T', Items.GOLD_NUGGET)
                .input('M', Items.NETHERITE_INGOT)
                .input('G', gem)
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(gem))
                .offerTo(exporter);
    }

    // ========================================
    // SPECIAL ITEM RECIPES
    // ========================================

    private void generateSpecialItems(RecipeExporter exporter) {
        jewelryKit(exporter);
    }

    /**
     * Generate the jewelry kit recipe.
     * Pattern: "CIG" / "###"
     * Special: Uses misc category and disables notification
     */
    private void jewelryKit(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, JewelryBlocks.JEWELERS_KIT.item())
                .pattern("CIG")
                .pattern("###")
                .input('C', Items.COPPER_INGOT)
                .input('I', Items.IRON_INGOT)
                .input('G', Items.GOLD_INGOT)
                .input('#', ItemTags.PLANKS)
                .criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
                .showNotification(false)
                .offerTo(exporter);
    }
}
