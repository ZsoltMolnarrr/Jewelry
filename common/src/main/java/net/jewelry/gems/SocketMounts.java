package net.jewelry.gems;

import net.jewelry.JewelryMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import java.util.ArrayList;
import java.util.List;

/// The socket mount items Jewelry ships. Each is a plain [SocketMountItem] whose default
/// [GemComponents#SOCKET_MOUNT] component says what it fits; add a variant by adding an entry and a tag.
public class SocketMounts {
    public record Entry(Identifier id, TagKey<Item> targets, Item item) { }

    public static final List<Entry> all = new ArrayList<>();

    /// `#jewelry:socket_mountable/armor` — the vanilla armor tags plus conventional `c:armors` (datagen).
    public static final TagKey<Item> ARMOR_TARGETS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(JewelryMod.ID, "socket_mountable/armor"));

    private static Entry mount(String name, TagKey<Item> targets, int sockets, int maxMounted) {
        var id = Identifier.fromNamespaceAndPath(JewelryMod.ID, name);
        // 1.21.2+: a tag's entry list is resolved through the registry's entry lookup (the tag binds later).
        var targetItems = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ITEM).getOrThrow(targets);
        var component = new SocketMountComponent(targetItems, sockets, SocketMountComponent.DEFAULT_TYPE, maxMounted);
        var item = new SocketMountItem(new Item.Properties()
                // 1.21.2+: settings must carry the item's own registry key or the constructor throws `Item id not set`.
                .setId(ResourceKey.create(Registries.ITEM, id))
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
            Registry.register(BuiltInRegistries.ITEM, entry.id(), entry.item());
        }
    }
}
