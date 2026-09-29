package net.jewelry.fabric.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.jewelry.JewelryMod;
import net.jewelry.api.datagen.GemCutGenerator;
import net.jewelry.api.datagen.SocketTypeGenerator;
import net.jewelry.api.datagen.SocketTypeTagGenerator;
import net.jewelry.gems.SocketMountComponent;
import net.jewelry.gems.SocketTypes;
import net.jewelry.gems.GemCuts;
import net.jewelry.gems.SocketMounts;
import net.minecraft.tags.ItemTags;
import net.jewelry.items.Gems;
import net.jewelry.items.JewelryItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_engine.rpg_series.datagen.RPGSeriesDataGen;

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
        pack.addProvider(JewelryGemCuts::new);
        pack.addProvider(JewelrySocketTypes::new);
        pack.addProvider(JewelrySocketTypeTags::new);
    }

    // ========================================
    // ITEM TAG GENERATION
    // ========================================

    public static class ItemTagGenerator extends RPGSeriesDataGen.ItemTagGenerator {
        public ItemTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            // Generate custom Jewelry tags
            generateJewelryTags();

            // Generate loot tier tags using RPGSeries helper
            generateLootTierTags();

            // What the armor socket mount fits: vanilla's armor tags plus the conventional c:armors
            builder(SocketMounts.ARMOR_TARGETS)
                    .addOptionalTag(ItemTags.HEAD_ARMOR)
                    .addOptionalTag(ItemTags.CHEST_ARMOR)
                    .addOptionalTag(ItemTags.LEG_ARMOR)
                    .addOptionalTag(ItemTags.FOOT_ARMOR)
                    .addOptionalTag(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "armors")));
        }

        /**
         * Generate jewelry-specific tags: gems, rings, and necklaces
         */
        private void generateJewelryTags() {
            // 1.21.6 split the tag-provider API: `getOrCreateTagBuilder` → key-based `builder(TagKey)`,
            // whose values are `RegistryKey`s rather than `Identifier`s.

            // jewelry:gems tag
            var gemsTag = builder(TagKey.create(Registries.ITEM,
                    Identifier.fromNamespaceAndPath(JewelryMod.ID, "gems")));
            Gems.all.forEach(gem -> gemsTag.addOptional(ResourceKey.create(Registries.ITEM, gem.id())));

            // jewelry:rings tag
            var ringsTag = builder(TagKey.create(Registries.ITEM,
                    Identifier.fromNamespaceAndPath(JewelryMod.ID, "rings")));
            JewelryItems.all.stream()
                    .filter(entry -> entry.id().getPath().contains("ring"))
                    .forEach(entry -> ringsTag.addOptional(ResourceKey.create(Registries.ITEM, entry.id())));

            // jewelry:necklaces tag
            var necklacesTag = builder(TagKey.create(Registries.ITEM,
                    Identifier.fromNamespaceAndPath(JewelryMod.ID, "necklaces")));
            JewelryItems.all.stream()
                    .filter(entry -> entry.id().getPath().contains("necklace"))
                    .forEach(entry -> necklacesTag.addOptional(ResourceKey.create(Registries.ITEM, entry.id())));
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

    /// 1.21.4 moved the model datagen classes to `net.minecraft.client.data` and Fabric's provider to
    /// `api.client.datagen.v1.provider`. `ItemModelGenerator#register` now emits **two** files per item:
    /// the model itself (`models/item/<id>.json`) and the item-model *definition*
    /// (`items/<id>.json`) that the `minecraft:item_model` component resolves.
    public static class ModelProvider extends FabricModelProvider {
        public ModelProvider(FabricPackOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {

        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerator) {
            Gems.all.forEach(gem -> {
                itemModelGenerator.generateFlatItem(gem.item(), ModelTemplates.FLAT_ITEM);
            });
            SocketMounts.all.forEach(mount -> {
                itemModelGenerator.generateFlatItem(mount.item(), ModelTemplates.FLAT_ITEM);
            });
            JewelryItems.all.forEach(entry -> {
                itemModelGenerator.generateFlatItem(entry.item(), ModelTemplates.FLAT_ITEM);
            });
            // Default cut sprite per gem (`models/item/gem_cut/<gem>.json`) plus one per cut with a custom icon;
            // all discovered under models/item/gem_cut/ and baked at runtime.
            Gems.all.forEach(gem -> GemCutGenerator.generateDefaultGemModel(itemModelGenerator, gem.item()));
            GemCuts.all.forEach(cut -> GemCutGenerator.generateItemModel(itemModelGenerator, cut));
        }
    }

    /// Jewelry's own cuts, through the public generator other mods use too.
    public static class JewelryGemCuts extends GemCutGenerator {
        public JewelryGemCuts(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void generate(Entries entries) {
            GemCuts.all.forEach(entries::add);
        }

        @Override
        public String getName() {
            return "Jewelry Gem Cuts";
        }
    }

    /// Jewelry's own socket types (the mounted socket's look), through the public generator.
    public static class JewelrySocketTypes extends SocketTypeGenerator {
        public JewelrySocketTypes(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void generate(Entries entries) {
            SocketTypes.all.forEach(entries::add);
        }

        @Override
        public String getName() {
            return "Jewelry Socket Types";
        }
    }

    /// `#jewelry:grindstone_removable`: the grindstone takes mounted sockets off again.
    public static class JewelrySocketTypeTags extends SocketTypeTagGenerator {
        public JewelrySocketTypeTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            grindstoneRemovable(SocketMountComponent.DEFAULT_TYPE);
        }
    }

    public static class UnsmeltGenerator extends FabricRecipeProvider {
        public UnsmeltGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        public static int UNSMELT_TIME = 300;

        @Override
        public String getName() {
            return "Jewelry Unsmelt Recipes";
        }

        /// 26.3: recipes and their advancements are written through two `BootstrapContext`s instead of a
        /// `RecipeOutput`; the generator builds its own `output` from them (vanilla `RecipeProvider`).
        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries,
                                                      BootstrapContext<Recipe<?>> recipeOutput,
                                                      BootstrapContext<Advancement> advancementOutput) {
            return new Generator(recipeOutput, advancementOutput);
        }

        private static class Generator extends RecipeProvider {
            Generator(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
                super(recipeOutput, advancementOutput);
            }

            @Override
            public void buildRecipes() {
                disassemble(List.of(JewelryItems.gold_ring.item()), Items.GOLD_NUGGET);
                disassemble(List.of(JewelryItems.iron_ring.item()), Items.IRON_NUGGET);
                disassemble(List.of(JewelryItems.emerald_necklace.item()), Items.EMERALD);
                disassemble(List.of(JewelryItems.diamond_necklace.item()), Items.DIAMOND);
                disassemble(
                        JewelryItems.all.stream()
                                .filter(entry -> entry.tier() == 2)
                                .map(entry -> (ItemLike) entry.item()).toList(),
                        Items.GOLD_NUGGET);
                disassemble(
                        JewelryItems.all.stream()
                                .filter(entry -> entry.id().getPath().contains("netherite"))
                                .map(entry -> (ItemLike) entry.item()).toList(),
                        Items.NETHERITE_SCRAP);
            }

            private void disassemble(List<ItemLike> items, Item output) {
                // 26.1: `oreSmelting`/`oreBlasting` take the `CookingBookCategory` explicitly
                // (it used to be derived inside `SimpleCookingRecipeBuilder.generic`). Reproduce
                // vanilla's rule rather than hardcoding, so a block-item result keeps `blocks`.
                var bookCategory = output instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
                oreSmelting(
                        items,
                        RecipeCategory.MISC,
                        bookCategory,
                        output,
                        0.1f,
                        UNSMELT_TIME,
                        "disassemble"
                );
                oreBlasting(
                        items,
                        RecipeCategory.MISC,
                        bookCategory,
                        output,
                        0.1f,
                        UNSMELT_TIME / 2,
                        "disassemble"
                );
            }
        }
    }
}
