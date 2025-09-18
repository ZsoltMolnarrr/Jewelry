package net.jewelry.neoforge;

import net.jewelry.JewelryMod;
import net.jewelry.neoforge.compat.CompatFeatures;
import net.neoforged.fml.common.Mod;

@Mod(JewelryMod.ID)
public final class NeoForgeMod {
    public NeoForgeMod() {
        CompatFeatures.init();
        JewelryMod.init();
    }
}
