package net.jewelry.gems;

import net.jewelry.JewelryMod;
import net.jewelry.api.bonus.AttributeBonus;
import net.jewelry.api.bonus.GemBonus;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
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

    // MARK: Which gem fits which socket

    /// Blacklist for standard sockets: a cut in `#jewelry:restricted` fits only sockets whose type definition
    /// `accepts` it. Every other cut — Jewelry's, any pack's — fits standard sockets with no extra step.
    public static final TagKey<GemCut> RESTRICTED = TagKey.of(GemCutRegistry.KEY, Identifier.of(JewelryMod.ID, "restricted"));

    /// A socket whose type has a definition with `accepts` takes exactly the cuts in that tag. Any other
    /// socket (untyped, or a type without a definition or without `accepts`) is a standard socket and takes
    /// every cut that is not [#RESTRICTED].
    public static boolean accepts(@Nullable RegistryWrapper.WrapperLookup lookup, SocketsComponent.Socket socket, RegistryEntry<GemCut> cut) {
        var filter = socket.type()
                .flatMap(type -> SocketTypeRegistry.find(lookup, type))
                .flatMap(SocketType::accepts);
        return filter.map(cut::isIn).orElseGet(() -> !cut.isIn(RESTRICTED));
    }

    /// Socket types the grindstone takes off an item (with the gems in them). Jewelry tags `jewelry:mounted`.
    public static final TagKey<SocketType> GRINDSTONE_REMOVABLE = TagKey.of(SocketTypeRegistry.KEY, Identifier.of(JewelryMod.ID, "grindstone_removable"));

    public static boolean hasSockets(ItemStack stack) {
        return of(stack).map(sockets -> sockets.count() > 0).orElse(false);
    }

    // MARK: Collecting bonuses

    /// A gem sitting in a socket: which socket, which cut, and the cut's bonus.
    public record SocketedBonus<T extends GemBonus>(int socketIndex, RegistryEntry<GemCut> cut, T bonus) {
        /// An id unique to this placement on this equipment slot: `<cut ns>:gem_cut/<cut>/<slot>/<index>`.
        /// Use it for anything that must not collapse when the same cut sits in two sockets or two slots
        /// (attribute modifiers, effect sources, …).
        public Identifier placementId(EquipmentSlot slot) {
            var cutId = GemCut.idOf(cut).orElse(Identifier.of(JewelryMod.ID, "unknown"));
            return cutId.withPrefixedPath("gem_cut/").withSuffixedPath("/" + slot.getName() + "/" + socketIndex);
        }
    }

    /// Every socketed gem of the stack with its bonus, in socket order. Empty while sockets are disabled.
    /// This is the discovery helper for custom bonus kinds: call it from whatever hook your mechanic needs.
    public static List<SocketedBonus<GemBonus>> bonuses(ItemStack stack) {
        return bonuses(stack, GemBonus.class);
    }

    /// The socketed gems of the stack whose bonus is of the given kind.
    public static <T extends GemBonus> List<SocketedBonus<T>> bonuses(ItemStack stack, Class<T> kind) {
        var sockets = of(stack).orElse(null);
        if (sockets == null || sockets.filled() == 0) {
            return List.of();
        }
        var found = new ArrayList<SocketedBonus<T>>();
        for (int i = 0; i < sockets.count(); i++) {
            var gem = sockets.gemAt(i);
            if (gem.isEmpty()) {
                continue;
            }
            var bonus = gem.get().value().bonus();
            if (kind.isInstance(bonus)) {
                found.add(new SocketedBonus<>(i, gem.get(), kind.cast(bonus)));
            }
        }
        return found;
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
        if (!appliesTo(stack, slot)) {
            return;
        }
        for (var socketed : bonuses(stack, AttributeBonus.class)) {
            socketed.bonus().resolveAttribute().ifPresent(attribute ->
                    consumer.accept(attribute, socketed.bonus().modifier(socketed.placementId(slot))));
        }
    }
}
