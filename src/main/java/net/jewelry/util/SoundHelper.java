package net.jewelry.util;

import net.jewelry.JewelryMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundHelper {
    public static final Identifier JEWELRY_EQUIP_ID = new Identifier(JewelryMod.ID, "jewelry_equip");
    public static final SoundEvent JEWELRY_EQUIP = SoundEvent.of(JEWELRY_EQUIP_ID);
    public static void register() {
        Registry.register(Registries.SOUND_EVENT, JEWELRY_EQUIP_ID, JEWELRY_EQUIP);
    }
}
