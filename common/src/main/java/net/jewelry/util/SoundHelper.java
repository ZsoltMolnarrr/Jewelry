package net.jewelry.util;

import net.jewelry.JewelryMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundHelper {
//    public record Entry(Identifier id, SoundEvent sound, RegistryEntry<SoundEvent> entry) {}
//
//    private static Entry registerSound(String key) {
//        var soundId = Identifier.of(JewelryMod.ID, key);
//        var event = SoundEvent.of(soundId);
//        var entry = Registry.registerReference(Registries.SOUND_EVENT, soundId, event);
//        return new Entry(soundId, event, entry);
//    }

    public static final Identifier JEWELRY_EQUIP_ID = Identifier.of(JewelryMod.ID, "jewelry_equip");
    public static final SoundEvent JEWELRY_EQUIP = SoundEvent.of(JEWELRY_EQUIP_ID);
    public static final Identifier JEWELRY_WORKBENCH_ID = Identifier.of(JewelryMod.ID, "jewelry_workbench");
    public static final SoundEvent JEWELRY_WORKBENCH = SoundEvent.of(JEWELRY_WORKBENCH_ID);

    public static void register() {
        Registry.register(Registries.SOUND_EVENT, JEWELRY_EQUIP_ID, JEWELRY_EQUIP);
        Registry.register(Registries.SOUND_EVENT, JEWELRY_WORKBENCH_ID, JEWELRY_WORKBENCH);
    }
}
