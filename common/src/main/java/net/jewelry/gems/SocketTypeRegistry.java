package net.jewelry.gems;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

/// The synced datapack registry of [SocketType] definitions. Vanilla namespace on purpose, like `gem_cut`:
/// a definition lives at `data/<mod>/socket_type/<id>.json`. Jewelry ships one, `jewelry:mounted`.
public class SocketTypeRegistry {
    public static final Identifier ID = Identifier.withDefaultNamespace("socket_type");
    public static final ResourceKey<Registry<SocketType>> KEY = ResourceKey.createRegistryKey(ID);

    /// The definition of a socket type, or empty when the type has none (or no registries are at hand).
    public static Optional<SocketType> find(@Nullable HolderLookup.Provider lookup, Identifier type) {
        if (lookup == null) {
            return Optional.empty();
        }
        return lookup.lookup(KEY)
                .flatMap(registry -> registry.get(ResourceKey.create(KEY, type)))
                .map(entry -> entry.value());
    }

    /// Whether the socket type's definition is in `tag`. A type without a definition is in no tag: to tag a
    /// type, give it a (possibly empty) definition.
    public static boolean isIn(@Nullable HolderLookup.Provider lookup, Identifier type, TagKey<SocketType> tag) {
        if (lookup == null) {
            return false;
        }
        return lookup.lookup(KEY)
                .flatMap(registry -> registry.get(ResourceKey.create(KEY, type)))
                .map(entry -> entry.is(tag))
                .orElse(false);
    }
}
