package net.jewelry.gems;

import net.jewelry.JewelryMod;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.List;

/// The socket mount items Jewelry ships. Each is a plain [SocketMountItem] whose default
/// [GemComponents#SOCKET_MOUNT] component says what it fits; add a variant by adding an entry and a tag.
public class SocketMounts {
    public record Entry(Identifier id, TagKey<Item> targets, Item item) { }

    public static final List<Entry> all = new ArrayList<>();

    /// `#jewelry:socket_mountable/armor` — the vanilla armor tags plus conventional `c:armors` (datagen).
    public static final TagKey<Item> ARMOR_TARGETS = TagKey.of(RegistryKeys.ITEM, Identifier.of(JewelryMod.ID, "socket_mountable/armor"));

    private static Entry mount(String name, TagKey<Item> targets, int sockets, int maxMounted) {
        var id = Identifier.of(JewelryMod.ID, name);
        var component = new SocketMountComponent(Registries.ITEM.getOrCreateEntryList(targets), sockets, SocketMountComponent.DEFAULT_TYPE, maxMounted);
        var item = new SocketMountItem(new Item.Settings()
                .rarity(Rarity.UNCOMMON)
                .component(GemComponents.SOCKET_MOUNT, component));
        var entry = new Entry(id, targets, item);
        all.add(entry);
        return entry;
    }

    /// Adds one socket to a piece of armor; at most one mounted socket per piece.
    public static final Entry SOCKET_MOUNT = mount("socket_mount", ARMOR_TARGETS, 1, 1);

    public static void register() {
        for (var entry : all) {
            Registry.register(Registries.ITEM, entry.id(), entry.item());
        }
    }
}
