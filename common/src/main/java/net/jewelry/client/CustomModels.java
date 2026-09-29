package net.jewelry.client;

import net.jewelry.gems.GemComponents;
import net.jewelry.gems.GemCut;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/// Per-stack item models of the gem system.
///
/// A stack renders with a custom model when either
/// - it carries the explicit [GemComponents#ITEM_MODEL] component, or
/// - it is a cut gem: its custom icon if the cut opted in, else its gem's default cut model
///   ([GemCut#effectiveModelId]). A model without an item-model definition falls back to the raw gem's own.
///
/// 1.21.4+: stacks render through item-model DEFINITIONS (`assets/<ns>/items/<path>.json`), which the game
/// discovers and bakes by itself — no model registration needed. Cut data keeps naming the MODEL
/// (`jewelry:item/gem_cut/bold_ruby`, the same value as on 1.21.1); its definition is the same id without
/// the `item/` folder (`jewelry:gem_cut/bold_ruby` → `assets/jewelry/items/gem_cut/bold_ruby.json`), see
/// [GemCut#definitionId].
public class CustomModels {
    @Nullable
    public static Identifier definitionIdOf(ItemStack stack) {
        var explicit = stack.get(GemComponents.ITEM_MODEL);
        if (explicit != null) {
            return GemCut.definitionId(explicit);
        }
        return GemCut.of(stack).map(entry -> GemCut.definitionId(entry.value().effectiveModelId())).orElse(null);
    }
}
