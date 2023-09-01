package net.jewelry.config;

import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemConfig {
    public Map<String, Item> items = new HashMap();
    public static class AttributeModifier { AttributeModifier() { }
        public String id = "";
        public float value = 0;
        public EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.ADDITION;

        public AttributeModifier(String id, float value, EntityAttributeModifier.Operation operation) {
            this.id = id;
            this.value = value;
            this.operation = operation;
        }
    }
    public static class Item { Item() { }
        public List<AttributeModifier> attributes = List.of();
        public Item(List<AttributeModifier> attributes) {
            this.attributes = attributes;
        }
    }
}
