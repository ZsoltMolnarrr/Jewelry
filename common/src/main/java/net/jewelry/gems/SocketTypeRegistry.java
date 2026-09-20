package net.jewelry.gems;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/// The synced datapack registry of [SocketType] definitions. Vanilla namespace on purpose, like `gem_cut`:
/// a definition lives at `data/<mod>/socket_type/<id>.json`. Jewelry ships none.
public class SocketTypeRegistry {
    public static final Identifier ID = Identifier.ofVanilla("socket_type");
    public static final RegistryKey<Registry<SocketType>> KEY = RegistryKey.ofRegistry(ID);

    /// The definition of a socket type, or empty when the type has none (or no registries are at hand).
    public static Optional<SocketType> find(@Nullable RegistryWrapper.WrapperLookup lookup, Identifier type) {
        if (lookup == null) {
            return Optional.empty();
        }
        return lookup.getOptionalWrapper(KEY)
                .flatMap(registry -> registry.getOptional(RegistryKey.of(KEY, type)))
                .map(entry -> entry.value());
    }
}
