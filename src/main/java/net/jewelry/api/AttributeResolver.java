package net.jewelry.api;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.projectile_damage.api.EntityAttributes_ProjectileDamage;
import net.spell_power.api.attributes.SpellAttributes;

import java.util.HashMap;

public class AttributeResolver {
    private static final HashMap<Identifier, EntityAttribute> attributes = new HashMap<>();

    /**
     * Called upon initialization of this mod.
     */
    public static void setup() {
        if (FabricLoader.getInstance().isModLoaded("spell_power")) {
            SpellAttributes.all.forEach((id, attribute) -> {
                register(attribute.id, attribute.attribute);
            });
        }
        if (FabricLoader.getInstance().isModLoaded("projectile_damage")) {
            register(
                EntityAttributes_ProjectileDamage.attributeId,
                EntityAttributes_ProjectileDamage.GENERIC_PROJECTILE_DAMAGE
            );
        }
    }

    public static void register(Identifier id, EntityAttribute attribute) {
        attributes.put(id, attribute);
    }

    public static EntityAttribute get(Identifier id) {
        // Check for custom attribute
        var attribute = attributes.get(id);
        if (attribute == null) {
            Registries.ATTRIBUTE.get(id);
        }
        return attribute;
    }
}