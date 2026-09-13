package net.jewelry.gems;

import net.jewelry.JewelryMod;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.BiConsumer;

/// Item-scope socket logic. Deliberately knows nothing about armor or weapons in particular: any item can
/// carry sockets, and socketed gems apply whenever the item is equipped in a slot its own attribute
/// modifiers target.
///
/// Default socket counts come from data, so other mods (or packs) grant sockets without a code dependency:
/// an item in `#jewelry:sockets/N` has N empty sockets until a `jewelry:sockets` component says otherwise.
public class GemSockets {
    public static final TagKey<net.minecraft.item.Item> ONE_SOCKET = tag("sockets/1");
    public static final TagKey<net.minecraft.item.Item> TWO_SOCKETS = tag("sockets/2");
    public static final TagKey<net.minecraft.item.Item> THREE_SOCKETS = tag("sockets/3");

    private static TagKey<net.minecraft.item.Item> tag(String path) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of(JewelryMod.ID, path));
    }

    /// Sockets an item has by default, before any component is written to the stack.
    public static int defaultCount(ItemStack stack) {
        if (stack.isIn(THREE_SOCKETS)) return 3;
        if (stack.isIn(TWO_SOCKETS)) return 2;
        if (stack.isIn(ONE_SOCKET)) return 1;
        return 0;
    }

    /// The effective sockets of a stack: its component if present, else the data default, else none.
    public static Optional<SocketsComponent> of(ItemStack stack) {
        var component = stack.get(GemComponents.SOCKETS);
        if (component != null) {
            return Optional.of(component);
        }
        var count = defaultCount(stack);
        return count > 0 ? Optional.of(SocketsComponent.empty(count)) : Optional.empty();
    }

    public static boolean hasSockets(ItemStack stack) {
        return of(stack).map(sockets -> sockets.count() > 0).orElse(false);
    }

    // MARK: Attribute contribution

    /// Whether socketed gems count while the item sits in `slot`: the slots the item's own modifiers
    /// target, falling back to the wearable slot for attribute-less equipment and the main hand otherwise.
    public static boolean appliesTo(ItemStack stack, EquipmentSlot slot) {
        var component = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        var modifiers = component.modifiers().isEmpty() ? stack.getItem().getAttributeModifiers() : component;
        if (!modifiers.modifiers().isEmpty()) {
            return modifiers.modifiers().stream().anyMatch(entry -> entry.slot().matches(slot));
        }
        if (stack.getItem() instanceof Equipment equipment) {
            return equipment.getSlotType() == slot;
        }
        return slot == EquipmentSlot.MAINHAND;
    }

    /// Hands every socketed gem's modifier to `consumer`, the way enchantment attribute effects are applied.
    /// Ids are unique per (cut, slot, socket index), so the same cut stacks across sockets and across slots.
    public static void applyModifiers(ItemStack stack, EquipmentSlot slot,
                                      BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> consumer) {
        var sockets = stack.get(GemComponents.SOCKETS);
        if (sockets == null || sockets.gems().isEmpty() || !appliesTo(stack, slot)) {
            return;
        }
        for (int i = 0; i < sockets.filled(); i++) {
            var gem = sockets.gems().get(i);
            var cutId = GemCut.idOf(gem);
            if (cutId.isEmpty()) {
                continue;
            }
            var modifierId = cutId.get().withPrefixedPath("gem_cut/").withSuffixedPath("/" + slot.getName() + "/" + i);
            var cut = gem.value();
            cut.resolveAttribute().ifPresent(attribute ->
                    consumer.accept(attribute, cut.modifier(modifierId)));
        }
    }
}
