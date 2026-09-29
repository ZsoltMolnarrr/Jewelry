package net.jewelry.gems;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/// The synced datapack registry of [SocketType] definitions. Vanilla namespace on purpose, like `gem_cut`:
/// a definition lives at `data/<mod>/socket_type/<id>.json`. Jewelry ships one, `jewelry:mounted`.
public class SocketTypeRegistry {
    public static final Identifier ID = Identifier.ofVanilla("socket_type");
    public static final RegistryKey<Registry<SocketType>> KEY = RegistryKey.ofRegistry(ID);

    /// The definition of a socket type, or empty when the type has none (or no registries are at hand).
    public static Optional<SocketType> find(@Nullable RegistryWrapper.WrapperLookup lookup, Identifier type) {
        if (lookup == null) {
            return Optional.empty();
        }
        return lookup.getOptional(KEY)
                .flatMap(registry -> registry.getOptional(RegistryKey.of(KEY, type)))
                .map(entry -> entry.value());
    }

    /// Whether the socket type's definition is in `tag`. A type without a definition is in no tag: to tag a
    /// type, give it a (possibly empty) definition.
    public static boolean isIn(@Nullable RegistryWrapper.WrapperLookup lookup, Identifier type, TagKey<SocketType> tag) {
        if (lookup == null) {
            return false;
        }
        return lookup.getOptional(KEY)
                .flatMap(registry -> registry.getOptional(RegistryKey.of(KEY, type)))
                .map(entry -> entry.isIn(tag))
                .orElse(false);
    }
}
