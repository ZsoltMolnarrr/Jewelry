package net.jewelry.client;

import net.jewelry.gems.GemComponents;
import net.jewelry.gems.GemCut;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/// Per-stack item models, replicating SpellEngine's `item_model` component (itself a backport of the
/// vanilla 1.21.2+ `minecraft:item_model` component) on 1.21.1.
///
/// A stack renders with a custom model when either
/// - it carries the explicit [GemComponents#ITEM_MODEL] component, or
/// - it is a cut gem whose [GemCut#model] is set.
///
/// Models are not bound to an item, so they must be registered for baking: every loader scans
/// `assets/<ns>/models/item/gem_cut/*.json` via [#discover] at resource load — cuts added by resource
/// packs are picked up without code. Ids are plain (`jewelry:item/gem_cut/bold_ruby`); the renderer mixin
/// applies the loader-specific wrapping when looking them up.
public class CustomModels {
    private static final Logger LOGGER = LoggerFactory.getLogger("Jewelry/CustomModels");
    public static final String MODEL_FOLDER = "models";
    public static final String GEM_CUT_MODEL_PATH = "item/gem_cut/";

    /// Conventional model id of a shipped cut: `<ns>:item/gem_cut/<cut>`.
    public static Identifier gemCutModelId(Identifier cutId) {
        return Identifier.of(cutId.getNamespace(), GEM_CUT_MODEL_PATH + cutId.getPath());
    }

    @Nullable
    public static Identifier modelIdOf(ItemStack stack) {
        var explicit = stack.get(GemComponents.ITEM_MODEL);
        if (explicit != null) {
            return explicit;
        }
        return GemCut.of(stack).flatMap(entry -> entry.value().model()).orElse(null);
    }

    /// Model ids of every `models/item/gem_cut/*.json` across all loaded resource packs.
    public static List<Identifier> discover(ResourceManager resourceManager) {
        var found = new ArrayList<Identifier>();
        try {
            var resources = resourceManager.findResources(MODEL_FOLDER, id ->
                    id.getPath().startsWith(MODEL_FOLDER + "/" + GEM_CUT_MODEL_PATH) && id.getPath().endsWith(".json"));
            for (var resourceId : resources.keySet()) {
                var path = resourceId.getPath();
                path = path.substring((MODEL_FOLDER + "/").length(), path.length() - ".json".length());
                found.add(Identifier.of(resourceId.getNamespace(), path));
            }
        } catch (Exception e) {
            LOGGER.error("Error scanning for gem cut models", e);
        }
        LOGGER.info("Discovered {} gem cut models", found.size());
        return found;
    }
}
