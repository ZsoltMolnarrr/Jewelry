package net.jewelry.util;

import net.jewelry.JewelryMod;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class SoundHelper {
    public static final Identifier JEWELRY_EQUIP_ID = Identifier.fromNamespaceAndPath(JewelryMod.ID, "jewelry_equip");
    public static final SoundEvent JEWELRY_EQUIP = SoundEvent.createVariableRangeEvent(JEWELRY_EQUIP_ID);
    public static Holder<SoundEvent> JEWELRY_EQUIP_ENTRY;
    public static final Identifier JEWELRY_WORKBENCH_ID = Identifier.fromNamespaceAndPath(JewelryMod.ID, "jewelry_workbench");
    public static final SoundEvent JEWELRY_WORKBENCH = SoundEvent.createVariableRangeEvent(JEWELRY_WORKBENCH_ID);

    public static void register() {
        JEWELRY_EQUIP_ENTRY = Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, JEWELRY_EQUIP_ID, JEWELRY_EQUIP);
        Registry.register(BuiltInRegistries.SOUND_EVENT, JEWELRY_WORKBENCH_ID, JEWELRY_WORKBENCH);
    }
}