package net.jewelry.neoforge.compat.rei;

import me.shedaniel.rei.forge.REIPluginCommon;
import net.jewelry.compat.rei.JewelryReiCommonPlugin;

/**
 * NeoForge discovers REI plugins by scanning for these annotations, which ship only in REI's
 * NeoForge artifact — so the annotated type has to live here while the logic stays in `common`.
 */
@REIPluginCommon
public class JewelryReiCommonPluginNeoForge extends JewelryReiCommonPlugin {
}
