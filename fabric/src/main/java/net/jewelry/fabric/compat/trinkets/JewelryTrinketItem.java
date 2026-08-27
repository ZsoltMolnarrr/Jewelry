package net.jewelry.fabric.compat.trinkets;

import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.callback.TrinketCallback;
import net.jewelry.items.JewelryItem;
import net.jewelry.util.SoundHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;
import java.util.function.Consumer;

/// Jewelry piece worn in a Trinkets slot.
///
/// Trinkets Updated 4.0 dropped the `TrinketItem` base class: per-item behaviour is a
/// {@link TrinketCallback}, resolved by Trinkets through `item instanceof TrinketCallback`
/// (`TrinketCallback.getCallback`). Slot compatibility stays data driven — the
/// `data/trinkets/tags/item/{hand,offhand}/ring` and `chest/necklace` tags point at
/// `#jewelry:rings` / `#jewelry:necklaces`, which the default slot validator accepts —
/// so no `TrinketEquippable` component is needed.
///
/// Attribute bonuses are no longer an overridable `getModifiers`: they ride the
/// `trinkets:attribute_modifiers` component (see {@link TrinketsHelper}), exactly like the
/// Curios side rides `curios:attribute_modifiers`.
public class JewelryTrinketItem extends Item implements TrinketCallback, JewelryItem {
    private final String lore;

    public JewelryTrinketItem(Properties settings, String lore) {
        super(settings);
        this.lore = lore;
    }

    // 1.21.6+: `appendTooltip` takes a `TooltipDisplayComponent` and a `Consumer<Text>` sink.
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent,
                              Consumer<Component> textConsumer, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
        if (lore != null && !lore.isEmpty()) {
            textConsumer.accept(Component.translatable(lore).withStyle(ChatFormatting.ITALIC, ChatFormatting.GOLD));
        }
    }

    /// Right-click equips into the first free matching slot — the behaviour the old
    /// `TrinketItem#use` provided for free. Trinkets Updated routes `Item#use` through this.
    @Override
    public boolean canEquipFromUse(ItemStack stack, LivingEntity entity) {
        return true;
    }

    /// No Trinkets-side equip sound; it is played client side from {@link #onEquip} below,
    /// which also covers GUI equips and keeps the "don't replay on world join" guard.
    @Override
    public @Nullable Holder<SoundEvent> getEquipSound(ItemStack stack, TrinketSlotAccess slot, LivingEntity entity) {
        return null;
    }

    @Override
    public void onEquip(ItemStack stack, TrinketSlotAccess slot, LivingEntity entity) {
        TrinketCallback.super.onEquip(stack, slot, entity);

        if (entity.level().isClientSide() // Play sound only on client
                && entity.tickCount > 100      // Avoid playing sound on entering world / dimension
        ) {
            entity.playSound(SoundHelper.JEWELRY_EQUIP, 1.0F, 1.0F);
        }
    }
}
