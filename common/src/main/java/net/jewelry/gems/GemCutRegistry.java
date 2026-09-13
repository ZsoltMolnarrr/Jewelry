package net.jewelry.gems;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Optional;

/// The synced datapack registry of [GemCut] definitions.
///
/// Uses the vanilla namespace on purpose (like SpellEngine's `spell` and `equipment_set` registries) so a
/// cut lives at `data/<mod>/gem_cut/<id>.json` rather than `data/<mod>/jewelry/gem_cut/<id>.json`.
public class GemCutRegistry {
    public static final Identifier ID = Identifier.ofVanilla("gem_cut");
    public static final RegistryKey<Registry<GemCut>> KEY = RegistryKey.ofRegistry(ID);

    public static Registry<GemCut> from(World world) {
        return world.getRegistryManager().get(KEY);
    }

    public static Optional<RegistryWrapper.Impl<GemCut>> from(RegistryWrapper.WrapperLookup lookup) {
        return lookup.getOptionalWrapper(KEY);
    }
}
