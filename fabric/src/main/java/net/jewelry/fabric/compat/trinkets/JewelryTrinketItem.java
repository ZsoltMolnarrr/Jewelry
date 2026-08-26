package net.jewelry.fabric.compat.trinkets;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.jewelry.items.JewelryItem;
import net.jewelry.util.SoundHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

public class JewelryTrinketItem extends TrinketItem implements JewelryItem {
    private ItemAttributeModifiers customAttributes = ItemAttributeModifiers.builder().build();
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

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier slotIdentifier) {
        var modifiers = super.getModifiers(stack, slot, entity, slotIdentifier);
        // `slotIdentifier` is already unique per equipped slot (…/<slot>/<index>), so bonuses
        // stack across slots. Tie the id to the item as well so quickly swapping a different
        // item within the same slot doesn't reuse an id and trip vanilla's "Modifier is already
        // applied" guard.
        var modifierId = slotIdentifier.withSuffix("/" + BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath());
        for (var entry : this.customAttributes.modifiers()) {
            modifiers.put(entry.attribute(),
                    new AttributeModifier(modifierId, entry.modifier().amount(), entry.modifier().operation()));
        }
        return modifiers;
    }

    public void setConfigurableModifiers(ItemAttributeModifiers component) {
        this.customAttributes = component;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onEquip(stack, slot, entity);

        if (entity.level().isClientSide() // Play sound only on client
                && entity.tickCount > 100      // Avoid playing sound on entering world / dimension
        ) {
            entity.playSound(SoundHelper.JEWELRY_EQUIP, 1.0F, 1.0F);
        }
    }
}