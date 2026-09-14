package net.jewelry.config;

/// `config/jewelry/features.json` — feature switches, read by the SERVER (the integrated one in singleplayer).
public class FeaturesConfig {
    /// Gem cuts & sockets. When false the server loads no `gem_cut` data at all (Jewelry's own and any pack's),
    /// so the synced registry reaches every client empty and nothing of the system shows: no cuts in the
    /// Gems tab, the Jeweler's Kit offers nothing, no socket lines on tooltips, no EMI category.
    /// Turning it off on a world that already has cut gems drops those stacks on load (their cut no longer
    /// exists), the same as removing a data pack cut. Re-read on every datapack (re)load, so `/reload` applies it.
    public boolean gem_cuts = true;

    /// Equipment sockets. Runtime switch, read on each side from its own config: when false no item resolves any
    /// sockets — no socket tooltip lines, socketed gems grant nothing, the anvil refuses to socket. The
    /// `jewelry:sockets` component stays on stacks untouched, so flipping it back restores everything.
    public boolean sockets = true;
}
