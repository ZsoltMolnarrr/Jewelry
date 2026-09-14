package net.jewelry.gems;

import net.jewelry.JewelryMod;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Optional;
import java.util.function.BiConsumer;

/// Item-scope socket logic. Deliberately knows nothing about armor or weapons in particular: any item can
/// carry sockets, and socketed gems apply whenever the item is equipped in a slot its own attribute
/// modifiers target.
///
/// An item declares its sockets as a default component in its item settings
/// (`settings.component(GemComponents.SOCKETS, SocketsComponent.empty(1))`, see [SocketsComponent#empty]);
/// the stack's component then overrides it once gems are socketed.
public class GemSockets {
    /// Runtime feature switch (`features.sockets`); each side reads its own config.
    public static boolean enabled() {
        return JewelryMod.featuresConfig.value.sockets;
    }

    /// The sockets of a stack: its `jewelry:sockets` component (the item's default counts), or none —
    /// always none while sockets are disabled, which is what every consumer (tooltip, anvil, attributes) checks.
    public static Optional<SocketsComponent> of(ItemStack stack) {
        if (!enabled()) {
            return Optional.empty();
        }
        return Optional.ofNullable(stack.get(GemComponents.SOCKETS));
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
        var sockets = of(stack).orElse(null);
        if (sockets == null || sockets.filled() == 0 || !appliesTo(stack, slot)) {
            return;
        }
        for (int i = 0; i < sockets.count(); i++) {
            var socket = sockets.gemAt(i);
            if (socket.isEmpty()) {
                continue;
            }
            var gem = socket.get();
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
