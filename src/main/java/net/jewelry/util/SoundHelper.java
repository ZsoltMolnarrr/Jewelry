package net.jewelry.util;

import net.jewelry.JewelryMod;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SoundHelper {
    public static final Identifier JEWELRY_EQUIP_ID = new Identifier(JewelryMod.ID, "jewelry_equip");
    public static final SoundEvent JEWELRY_EQUIP = new SoundEvent(JEWELRY_EQUIP_ID);
    public static final Identifier JEWELRY_WORKBENCH_ID = new Identifier(JewelryMod.ID, "jewelry_workbench");
    public static final SoundEvent JEWELRY_WORKBENCH = new SoundEvent(JEWELRY_WORKBENCH_ID);

    public static void register() {
        Registry.register(Registry.SOUND_EVENT, JEWELRY_EQUIP_ID, JEWELRY_EQUIP);
        Registry.register(Registry.SOUND_EVENT, JEWELRY_WORKBENCH_ID, JEWELRY_WORKBENCH);
    }
}
