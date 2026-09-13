package net.jewelry.gems;

import net.jewelry.JewelryMod;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.util.Identifier;

/// Data component types of the gem system.
///
/// The types are built statically but registered from [#register()], which each loader calls at the
/// right moment (Fabric: mod init; NeoForge: the `DATA_COMPONENT_TYPE` `RegisterEvent`), because
/// NeoForge freezes vanilla registries outside its register events.
public class GemComponents {
    public static final Identifier CUT_ID = Identifier.of(JewelryMod.ID, "cut");

    /// Present on a cut gem, referencing its [GemCut]. Absent on a raw gem.
    public static final ComponentType<RegistryEntry<GemCut>> CUT = ComponentType.<RegistryEntry<GemCut>>builder()
            .codec(RegistryFixedCodec.of(GemCutRegistry.KEY))
            .packetCodec(PacketCodecs.registryEntry(GemCutRegistry.KEY))
            .build();

    public static final Identifier SOCKETS_ID = Identifier.of(JewelryMod.ID, "sockets");

    /// Present on an item that has (or had) sockets written to it; see [GemSockets#of] for the data default.
    public static final ComponentType<SocketsComponent> SOCKETS = ComponentType.<SocketsComponent>builder()
            .codec(SocketsComponent.CODEC)
            .packetCodec(SocketsComponent.PACKET_CODEC)
            .build();

    public static final Identifier ITEM_MODEL_ID = Identifier.of(JewelryMod.ID, "item_model");

    /// Explicit per-stack item model (a plain model id such as `jewelry:item/gem_cut/bold_ruby`), the
    /// SpellEngine `item_model` component replicated. Cut gems don't need it: their model comes from the cut.
    public static final ComponentType<Identifier> ITEM_MODEL = ComponentType.<Identifier>builder()
            .codec(Identifier.CODEC)
            .packetCodec(Identifier.PACKET_CODEC)
            .build();

    public static void register() {
        Registry.register(Registries.DATA_COMPONENT_TYPE, CUT_ID, CUT);
        Registry.register(Registries.DATA_COMPONENT_TYPE, ITEM_MODEL_ID, ITEM_MODEL);
        Registry.register(Registries.DATA_COMPONENT_TYPE, SOCKETS_ID, SOCKETS);
    }
}
