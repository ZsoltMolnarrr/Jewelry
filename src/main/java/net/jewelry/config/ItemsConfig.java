package net.jewelry.config;

import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemsConfig {
    public Map<String, Item> weapons = new HashMap();
    public static class Item { Item() { }
        public ArrayList<AttributeModifier> attributes = new ArrayList<>();
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

        public Item(ArrayList<AttributeModifier> attributes) {
            this.attributes = attributes;
        }
    }
}
