package net.jewelry.forge.compat.curios;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.jewelry.items.JewelryItem;
import net.jewelry.items.JewelryModifiers;
import net.jewelry.util.SoundHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class JewelryCurioItem extends Item implements ICurioItem, JewelryItem {
    private JewelryModifiers customAttributes = JewelryModifiers.EMPTY;
    private final String lore;

    public JewelryCurioItem(Item.Settings settings, String lore) {
        super(settings);
        this.lore = lore;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (lore != null && !lore.isEmpty()) {
            tooltip.add(Text.translatable(lore).formatted(Formatting.ITALIC, Formatting.GOLD));
        }
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        // Defensive copy: the default implementation's mutability is not part of the API contract.
        Multimap<EntityAttribute, EntityAttributeModifier> modifiers = LinkedHashMultimap.create();
        modifiers.putAll(ICurioItem.super.getAttributeModifiers(slotContext, uuid, stack));
        // `uuid` is already unique per equipped slot, so bonuses stack across slots; JewelryModifiers
        // folds the per-item modifier id into it so two attributes of one piece stay distinct and the
        // derivation stays deterministic (which is what lets Curios remove them again on unequip).
        modifiers.putAll(this.customAttributes.multimap(uuid));
        return modifiers;
    }

    public void setConfigurableModifiers(JewelryModifiers modifiers) {
        this.customAttributes = modifiers != null ? modifiers : JewelryModifiers.EMPTY;
    }

    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundHelper.JEWELRY_EQUIP, 1.0F, 1.0F);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        var entity = slotContext.entity();
        if (entity == null) {
            return;
        }
        var world = entity.getWorld();
        if (world.isClient()                        // the server broadcast below reaches every nearby client
                || entity.age <= 100                // gear already worn when entering a world/dimension
                || prevStack.isOf(stack.getItem())) // same item, only its data changed
        {
            return;
        }
        world.playSound(null, entity.getBlockPos(), SoundHelper.JEWELRY_EQUIP, entity.getSoundCategory(), 1.0F, 1.0F);
    }

    @Override
    public void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
        // Silent on purpose. `onEquip` above already fires for every equip path, this one included.
        // Curios' default would route through `ICurio`'s stateless instance, which resolves
        // `getEquipSound` on that instance rather than on this item, playing a generic sound.
    }
}
