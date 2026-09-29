package net.jewelry.gems;

import net.jewelry.JewelryMod;

import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// The synced datapack registry of [GemCut] definitions.
///
/// Uses the vanilla namespace on purpose (like SpellEngine's `spell` and `equipment_set` registries) so a
/// cut lives at `data/<mod>/gem_cut/<id>.json` rather than `data/<mod>/jewelry/gem_cut/<id>.json`.
public class GemCutRegistry {
    public static final Identifier ID = Identifier.withDefaultNamespace("gem_cut");
    public static final ResourceKey<Registry<GemCut>> KEY = ResourceKey.createRegistryKey(ID);

    public static Registry<GemCut> from(Level world) {
        return world.registryAccess().lookupOrThrow(KEY);
    }

    public static Optional<HolderLookup.RegistryLookup<GemCut>> from(HolderLookup.Provider lookup) {
        return lookup.lookup(KEY).map(registry -> (HolderLookup.RegistryLookup<GemCut>) registry);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger("Jewelry/GemCuts");

    // MARK: Feature switch

    /// Server side: whether `gem_cut` data may be loaded at all (`features.gem_cuts`). Re-reads the config
    /// so a `/reload` after editing it takes effect; called once per datapack load by the loader mixin.
    public static boolean loadingEnabled() {
        JewelryMod.featuresConfig.refresh();
        var enabled = JewelryMod.featuresConfig.safeValue().gem_cuts;
        if (!enabled) {
            LOGGER.info("Gem cuts are disabled in config/jewelry/features.json — loading no gem_cut data");
        }
        return enabled;
    }

    /// Everywhere else (both sides): the feature is "on" exactly when the synced registry has entries.
    /// A disabled server loads none, so clients need no flag of their own.
    public static boolean isEnabled(Level world) {
        return world != null && from(world).size() > 0;
    }

    public static boolean isEnabled(@org.jetbrains.annotations.Nullable HolderLookup.Provider lookup) {
        return lookup != null && from(lookup).map(registry -> registry.listElements().findAny().isPresent()).orElse(false);
    }

    /// One line at server start listing the loaded cuts — makes load-conditioned cuts (present only with
    /// their mod) verifiable from the log. Each loader calls this from its server-started hook.
    public static void logLoaded(MinecraftServer server) {
        var registry = server.registryAccess().lookupOrThrow(KEY);
        var ids = registry.keySet().stream().map(Identifier::toString).sorted().collect(Collectors.joining(", "));
        LOGGER.info("Loaded {} gem cuts: {}", registry.size(), ids);
        server.registryAccess().lookup(SocketTypeRegistry.KEY).ifPresent(types -> {
            if (types.size() > 0) {
                LOGGER.info("Loaded {} socket types: {}", types.size(),
                        types.keySet().stream().map(Identifier::toString).sorted().collect(Collectors.joining(", ")));
            }
        });
    }
}
