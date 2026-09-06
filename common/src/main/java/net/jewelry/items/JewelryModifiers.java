package net.jewelry.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;
import net.spell_power.api.ModifierDefinitions;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/// The 1.20.1 stand-in for 1.21's `AttributeModifiersComponent`.
///
/// 1.20.1 has no attribute-modifier data component and no `Identifier`-keyed modifiers: modifiers are
/// `Multimap<EntityAttribute, EntityAttributeModifier>` values keyed by `UUID`. This record keeps the
/// modern, namespaced modifier id as the source of truth and derives the UUID from it through
/// SpellPower's {@link ModifierDefinitions#uuid(Identifier)} bridge, so ids stay stable across worlds
/// and cannot collide across namespaces.
public record JewelryModifiers(List<Entry> entries) {
    /// One resolved bonus. `id` is the modern modifier identifier (`jewelry:<item>_<attribute>_bonus`).
    public record Entry(EntityAttribute attribute, Identifier id, double value,
                        EntityAttributeModifier.Operation operation) { }

    public static final JewelryModifiers EMPTY = new JewelryModifiers(List.of());

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /// Item-level modifiers, keyed by the stable per-item modifier id. Used by the vanilla fallback item
    /// (no slot mod installed), where there is no equipped-slot identity to scope them to.
    public Multimap<EntityAttribute, EntityAttributeModifier> multimap() {
        var builder = ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder();
        for (var entry : entries) {
            builder.put(entry.attribute(), new EntityAttributeModifier(
                    ModifierDefinitions.uuid(entry.id()),
                    ModifierDefinitions.name(entry.id()),
                    entry.value(),
                    entry.operation()));
        }
        return builder.build();
    }

    /// Modifiers scoped to one equipped accessory slot. Trinkets and Curios both hand the item a `UUID`
    /// that is already unique per equipped slot, so bonuses stack across slots; folding the modifier id
    /// into it keeps two different attributes of the same item apart and keeps the derivation
    /// deterministic, which is what lets the slot mod remove exactly these modifiers on unequip.
    public Multimap<EntityAttribute, EntityAttributeModifier> multimap(UUID slotUuid) {
        var builder = ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder();
        for (var entry : entries) {
            var name = slotUuid + "/" + entry.id();
            builder.put(entry.attribute(), new EntityAttributeModifier(
                    UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)),
                    name,
                    entry.value(),
                    entry.operation()));
        }
        return builder.build();
    }
}
