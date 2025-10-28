package net.jewelry.fabric.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.jewelry.JewelryMod;
import net.jewelry.compat.AccessoriesCompat;
import net.jewelry.fabric.compat.trinkets.TrinketsCompat;
import net.tiny_config.ConfigManager;

public class CompatFeatures {
    private static final ConfigManager<FabricCompatConfig> config = new ConfigManager<>
            ("fabric_compatibility", new FabricCompatConfig())
            .builder()
            .setDirectory(JewelryMod.ID)
            .sanitize(true)
            .build();

    public static void init() {
        initSlotCompat();
    }

    private static void initSlotCompat() {
        var loadedConfig = config.safeValue();
        var preferred = loadedConfig.preferred_slot_mod;
        try {
            if (FabricLoader.getInstance().isModLoaded("spell_engine")) {
                preferred = SpellEngineHelper.initSlotCompat();
            }
        } catch (Exception e) { }

        if ("trinkets".equals(preferred)) {
            if (initTrinkets()) {
                return;
            }
        } else if ("accessories".equals(preferred)) {
            if (initAccessories()) {
                return;
            }
        }
        if (initTrinkets()) {
            return;
        }
        initAccessories();
    }

    private static boolean initTrinkets() {
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            TrinketsCompat.init();
            return true;
        }
        return false;
    }

    private static boolean initAccessories() {
        if (FabricLoader.getInstance().isModLoaded("accessories")) {
            AccessoriesCompat.init();
            return true;
        }
        return false;
    }
}
