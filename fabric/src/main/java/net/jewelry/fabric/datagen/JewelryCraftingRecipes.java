package net.jewelry.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.items.Gems;
import net.jewelry.items.JewelryItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;

public class JewelryCraftingRecipes extends FabricRecipeProvider {
    public JewelryCraftingRecipes(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "Jewelry Crafting Recipes";
    }

    /// Since 1.21.2 the recipe provider only *builds* a {@link RecipeGenerator}; the recipe-building
    /// helpers (`createShaped`, `hasItem`, `conditionsFromItem`) are instance members of the generator,
    /// which also holds the exporter.
    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput exporter) {
        return new Generator(registries, exporter);
    }

    private static class Generator extends RecipeProvider {
        Generator(HolderLookup.Provider registries, RecipeOutput exporter) {
            super(registries, exporter);
        }

        @Override
        public void buildRecipes() {
            generateBasicRings();
            generateGemJewelry();
            generateGemRings();
            generateGemNecklaces();
            generateNetheriteRings();
            generateNetheriteNecklaces();
            generateSpecialItems();
        }

        // ========================================
        // BASIC RING RECIPES
        // ========================================

        private void generateBasicRings() {
            metalRing(JewelryItems.copper_ring.item(), Items.COPPER_INGOT);
            metalRing(JewelryItems.iron_ring.item(), Items.IRON_INGOT);
            metalRing(JewelryItems.gold_ring.item(), Items.GOLD_INGOT);
        }

        /**
         * Generate a simple metal ring recipe.
         * Pattern: " M " / "M M" / " M "
         */
        private void metalRing(Item result, Item metal) {
            shaped(RecipeCategory.COMBAT, result)
                    .pattern(" M ")
                    .pattern("M M")
                    .pattern(" M ")
                    .define('M', metal)
                    .unlockedBy(getHasName(metal), has(metal))
                    .save(output);
        }

        // ========================================
        // VANILLA Jewelry
        // ========================================

        private void generateGemJewelry() {
            vanillaNecklace(JewelryItems.diamond_necklace.item(), Items.DIAMOND);
            gemRing(JewelryItems.diamond_ring.item(), Items.DIAMOND);
            vanillaNecklace(JewelryItems.emerald_necklace.item(), Items.EMERALD);
        }

        /**
         * Generate a vanilla necklace recipe (diamond, emerald).
         * Pattern: " S " / " M " / " G "
         */
        private void vanillaNecklace(Item result, Item gem) {
            shaped(RecipeCategory.COMBAT, result)
                    .pattern(" S ")
                    .pattern(" M ")
                    .pattern(" G ")
                    .define('S', Items.STRING)
                    .define('M', Items.GOLD_INGOT)
                    .define('G', gem)
                    .unlockedBy(getHasName(gem), has(gem))
                    .save(output);
        }

        // ========================================
        // GEM RING RECIPES
        // ========================================

        private void generateGemRings() {
            gemRing(JewelryItems.ruby_ring.item(), Gems.ruby.item());
            gemRing(JewelryItems.topaz_ring.item(), Gems.topaz.item());
            gemRing(JewelryItems.citrine_ring.item(), Gems.citrine.item());
            gemRing(JewelryItems.jade_ring.item(), Gems.jade.item());
            gemRing(JewelryItems.sapphire_ring.item(), Gems.sapphire.item());
            gemRing(JewelryItems.tanzanite_ring.item(), Gems.tanzanite.item());
        }

        /**
         * Generate a gem ring recipe.
         * Pattern: " G " / "M M" / " M "
         */
        private void gemRing(Item result, Item gem) {
            shaped(RecipeCategory.COMBAT, result)
                    .pattern(" G ")
                    .pattern("M M")
                    .pattern(" M ")
                    .define('G', gem)
                    .define('M', Items.GOLD_INGOT)
                    .unlockedBy(getHasName(gem), has(gem))
                    .save(output);
        }

        // ========================================
        // GEM NECKLACE RECIPES
        // ========================================

        private void generateGemNecklaces() {
            gemNecklace(JewelryItems.ruby_necklace.item(), Gems.ruby.item());
            gemNecklace(JewelryItems.topaz_necklace.item(), Gems.topaz.item());
            gemNecklace(JewelryItems.citrine_necklace.item(), Gems.citrine.item());
            gemNecklace(JewelryItems.jade_necklace.item(), Gems.jade.item());
            gemNecklace(JewelryItems.sapphire_necklace.item(), Gems.sapphire.item());
            gemNecklace(JewelryItems.tanzanite_necklace.item(), Gems.tanzanite.item());
        }

        /**
         * Generate a gem necklace recipe.
         * Pattern: " S " / " M " / " G "
         */
        private void gemNecklace(Item result, Item gem) {
            shaped(RecipeCategory.COMBAT, result)
                    .pattern(" S ")
                    .pattern(" M ")
                    .pattern(" G ")
                    .define('S', Items.STRING)
                    .define('M', Items.GOLD_INGOT)
                    .define('G', gem)
                    .unlockedBy(getHasName(gem), has(gem))
                    .save(output);
        }

        // ========================================
        // NETHERITE RING RECIPES
        // ========================================

        private void generateNetheriteRings() {
            netheriteRing(JewelryItems.netherite_ruby_ring.item(), Gems.ruby.item());
            netheriteRing(JewelryItems.netherite_topaz_ring.item(), Gems.topaz.item());
            netheriteRing(JewelryItems.netherite_citrine_ring.item(), Gems.citrine.item());
            netheriteRing(JewelryItems.netherite_jade_ring.item(), Gems.jade.item());
            netheriteRing(JewelryItems.netherite_sapphire_ring.item(), Gems.sapphire.item());
            netheriteRing(JewelryItems.netherite_tanzanite_ring.item(), Gems.tanzanite.item());
        }

        /**
         * Generate a netherite ring recipe.
         * Pattern: " G " / "M M" / " N "
         */
        private void netheriteRing(Item result, Item gem) {
            shaped(RecipeCategory.COMBAT, result)
                    .pattern(" G ")
                    .pattern("M M")
                    .pattern(" N ")
                    .define('G', gem)
                    .define('M', Items.GOLD_INGOT)
                    .define('N', Items.NETHERITE_INGOT)
                    .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(gem))
                    .save(output);
        }

        // ========================================
        // NETHERITE NECKLACE RECIPES
        // ========================================

        private void generateNetheriteNecklaces() {
            netheriteNecklace(JewelryItems.netherite_ruby_necklace.item(), Gems.ruby.item());
            netheriteNecklace(JewelryItems.netherite_topaz_necklace.item(), Gems.topaz.item());
            netheriteNecklace(JewelryItems.netherite_citrine_necklace.item(), Gems.citrine.item());
            netheriteNecklace(JewelryItems.nsetherite_jade_necklace.item(), Gems.jade.item());
            netheriteNecklace(JewelryItems.netherite_sapphire_necklace.item(), Gems.sapphire.item());
            netheriteNecklace(JewelryItems.netherite_tanzanite_necklace.item(), Gems.tanzanite.item());
        }

        /**
         * Generate a netherite necklace recipe.
         * Pattern: " S " / "TMT" / " G "
         */
        private void netheriteNecklace(Item result, Item gem) {
            shaped(RecipeCategory.COMBAT, result)
                    .pattern(" S ")
                    .pattern("TMT")
                    .pattern(" G ")
                    .define('S', Items.STRING)
                    .define('T', Items.GOLD_NUGGET)
                    .define('M', Items.NETHERITE_INGOT)
                    .define('G', gem)
                    .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(gem))
                    .save(output);
        }

        // ========================================
        // SPECIAL ITEM RECIPES
        // ========================================

        private void generateSpecialItems() {
            jewelryKit();
        }

        /**
         * Generate the jewelry kit recipe.
         * Pattern: "CIG" / "###"
         * Special: Uses misc category and disables notification
         */
        private void jewelryKit() {
            shaped(RecipeCategory.MISC, JewelryBlocks.JEWELERS_KIT.item())
                    .pattern("CIG")
                    .pattern("###")
                    .define('C', Items.COPPER_INGOT)
                    .define('I', Items.IRON_INGOT)
                    .define('G', Items.GOLD_INGOT)
                    .define('#', ItemTags.PLANKS)
                    .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                    .showNotification(false)
                    .save(output);
        }
    }
}
