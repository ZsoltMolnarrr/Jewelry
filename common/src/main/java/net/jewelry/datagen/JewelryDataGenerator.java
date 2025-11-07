package net.jewelry.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.jewelry.JewelryMod;
import net.jewelry.items.Gems;
import net.jewelry.items.JewelryItem;
import net.jewelry.items.JewelryItems;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.Equipment;
import net.spell_engine.rpg_series.datagen.RPGSeriesDataGen;
import net.spell_engine.rpg_series.tags.RPGSeriesItemTags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class JewelryDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(JewelryCraftingRecipes::new);
        pack.addProvider(ItemTagGenerator::new);
        pack.addProvider(ModelProvider::new);
        pack.addProvider(UnsmeltGenerator::new);
    }

    // ========================================
    // ITEM TAG GENERATION
    // ========================================

    public static class ItemTagGenerator extends RPGSeriesDataGen.ItemTagGenerator {
        public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            // Generate custom Jewelry tags
            generateJewelryTags();

            // Generate loot tier tags using RPGSeries helper
            generateLootTierTags();
        }

        /**
         * Generate jewelry-specific tags: gems, rings, and necklaces
         */
        private void generateJewelryTags() {
            // jewelry:gems tag
            var gemsTag = getOrCreateTagBuilder(TagKey.of(RegistryKeys.ITEM,
                    Identifier.of(JewelryMod.ID, "gems")));
            Gems.all.forEach(gem -> gemsTag.addOptional(gem.id()));

            // jewelry:rings tag
            var ringsTag = getOrCreateTagBuilder(TagKey.of(RegistryKeys.ITEM,
                    Identifier.of(JewelryMod.ID, "rings")));
            JewelryItems.all.stream()
                    .filter(entry -> entry.id().getPath().contains("ring"))
                    .forEach(entry -> ringsTag.addOptional(entry.id()));

            // jewelry:necklaces tag
            var necklacesTag = getOrCreateTagBuilder(TagKey.of(RegistryKeys.ITEM,
                    Identifier.of(JewelryMod.ID, "necklaces")));
            JewelryItems.all.stream()
                    .filter(entry -> entry.id().getPath().contains("necklace"))
                    .forEach(entry -> necklacesTag.addOptional(entry.id()));
        }

        /**
         * Generate loot tier tags for rpg_series:loot_tier/*
         */
        private void generateLootTierTags() {
            // Convert JewelryItems.Entry list to Map<Identifier, Equipment.LootProperties>
            Map<Identifier, Equipment.LootProperties> accessoriesMap = new HashMap<>();

            for (var entry : JewelryItems.all) {
                // Create LootProperties with tier information from the entry
                var lootProperties = new Equipment.LootProperties(entry.tier(), null);
                accessoriesMap.put(entry.id(), lootProperties);
            }

            // Use the helper method from RPGSeriesDataGen to generate loot tier tags
            generateAccessoryTags(accessoriesMap);
        }
    }

    // ========================================
    // MODEL GENERATION
    // ========================================

    public static class ModelProvider extends FabricModelProvider {
        public ModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            Gems.all.forEach(gem -> {
                itemModelGenerator.register(gem.item(), Models.GENERATED);
            });
            JewelryItems.all.forEach(entry -> {
                itemModelGenerator.register(entry.item(), Models.GENERATED);
            });
        }
    }

    public static class UnsmeltGenerator extends FabricRecipeProvider {
        public UnsmeltGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        public static int UNSMELT_TIME = 300;

        @Override
        public String getName() {
            return "Jewelry Unsmelt Recipes";
        }

        @Override
        public void generate(RecipeExporter exporter) {
            disassemble(exporter, List.of(JewelryItems.gold_ring.item()), Items.GOLD_NUGGET);
            disassemble(exporter, List.of(JewelryItems.iron_ring.item()), Items.IRON_NUGGET);
            disassemble(exporter, List.of(JewelryItems.emerald_necklace.item()), Items.EMERALD);
            disassemble(exporter, List.of(JewelryItems.diamond_necklace.item()), Items.DIAMOND);
            disassemble(exporter,
                    JewelryItems.all.stream()
                            .filter(entry -> entry.tier() == 2)
                            .map(entry -> (ItemConvertible) entry.item()).toList(),
                    Items.GOLD_NUGGET);
            disassemble(exporter,
                    JewelryItems.all.stream()
                            .filter(entry -> entry.id().getPath().contains("netherite"))
                            .map(entry -> (ItemConvertible) entry.item()).toList(),
                    Items.NETHERITE_SCRAP);
        }

        private static void disassemble(RecipeExporter exporter, List<ItemConvertible> items, Item output) {
            FabricRecipeProvider.offerSmelting(exporter,
                    items,
                    RecipeCategory.MISC,
                    output,
                    0.1f,
                    UNSMELT_TIME,
                    "disassemble"
            );
            FabricRecipeProvider.offerBlasting(exporter,
                    items,
                    RecipeCategory.MISC,
                    output,
                    0.1f,
                    UNSMELT_TIME / 2,
                    "disassemble"
            );
        }
    }
}
