package net.jewelry.gems;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger("Jewelry/GemCuts");

    /// One line at server start listing the loaded cuts — makes load-conditioned cuts (present only with
    /// their mod) verifiable from the log. Each loader calls this from its server-started hook.
    public static void logLoaded(MinecraftServer server) {
        var registry = server.getRegistryManager().get(KEY);
        var ids = registry.getIds().stream().map(Identifier::toString).sorted().collect(Collectors.joining(", "));
        LOGGER.info("Loaded {} gem cuts: {}", registry.size(), ids);
    }
}
