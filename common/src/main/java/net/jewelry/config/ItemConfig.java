package net.jewelry.config;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemConfig {
    public Map<String, Item> items = new HashMap();

    public record Bonus(float value, EntityAttributeModifier.Operation operation) { }
    public static class AttributeModifier { AttributeModifier() { }
        public String id = "";
        public float value = 0;
        public EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.ADD_VALUE;

        public AttributeModifier(Identifier id, float value, EntityAttributeModifier.Operation operation) {
            this(id.toString(), value, operation);
        }

        public AttributeModifier(Identifier id, Bonus bonus) {
            this(id.toString(), bonus.value, bonus.operation);
        }

        public AttributeModifier(String id, Bonus bonus) {
            this(id, bonus.value, bonus.operation);
        }

        public AttributeModifier(String id, float value, EntityAttributeModifier.Operation operation) {
            this.id = id;
            this.value = value;
            this.operation = operation;
        }
    }
    public record ConditionalAttributes(String required_mod, List<AttributeModifier> attributes) { }

    public static class Item { Item() { }
        public ConditionalAttributes conditional_attributes = null;
        public List<AttributeModifier> attributes = List.of();
        public Item(List<AttributeModifier> attributes) {
            this.attributes = attributes;
        }

        public List<AttributeModifier> selectedAttributes() {
            if (this.conditional_attributes != null
                    && this.conditional_attributes.required_mod != null
                    && FabricLoader.getInstance().isModLoaded(this.conditional_attributes.required_mod)) {
                return this.conditional_attributes.attributes();
            }
            return this.attributes;
        }
    }

    public static ItemConfig.Item item(List<AttributeModifier> attributes) {
        ItemConfig.Item config = new ItemConfig.Item(attributes);
        return config;
    }

    public static ItemConfig.Item itemWithCondition(String required_mod, List<AttributeModifier> conditionalAttributes, List<AttributeModifier> fallbackAttributes) {
        ItemConfig.Item config = new ItemConfig.Item(fallbackAttributes);
        config.conditional_attributes = new ConditionalAttributes(required_mod, conditionalAttributes);
        return config;
    }
}
