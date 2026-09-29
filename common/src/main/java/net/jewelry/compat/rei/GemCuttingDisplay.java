package net.jewelry.compat.rei;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.jewelry.JewelryMod;
import net.jewelry.gems.GemCut;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * REI's view of a single gem cut: raw gem → cut gem, worked at the Jeweler's Kit.
 * <p>
 * Displays are built on the client by {@link JewelryReiClientPlugin} from the synced {@code gem_cut}
 * registry, so they never come over REI's display sync. The serializer still exists so REI can persist
 * them (favorites).
 * <p>
 * Not client-only: the serializer registry runs on both sides.
 */
public class GemCuttingDisplay extends BasicDisplay {
    public static final String NAME = "gem_cutting";
    public static final CategoryIdentifier<GemCuttingDisplay> CATEGORY = CategoryIdentifier.of(JewelryMod.ID, NAME);

    public static final DisplaySerializer<GemCuttingDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(GemCuttingDisplay::getInputEntries),
                    EntryIngredient.codec().listOf().fieldOf("outputs").forGetter(GemCuttingDisplay::getOutputEntries),
                    Identifier.CODEC.optionalFieldOf("location").forGetter(GemCuttingDisplay::getDisplayLocation)
            ).apply(instance, GemCuttingDisplay::new)),
            PacketCodec.tuple(
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()), GemCuttingDisplay::getInputEntries,
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()), GemCuttingDisplay::getOutputEntries,
                    PacketCodecs.optional(Identifier.PACKET_CODEC), GemCuttingDisplay::getDisplayLocation,
                    GemCuttingDisplay::new));

    public GemCuttingDisplay(Identifier cutId, RegistryEntry<GemCut> cut) {
        this(List.of(EntryIngredients.of(cut.value().gem().value())),
                List.of(EntryIngredients.of(GemCut.stack(cut))),
                Optional.of(displayId(cutId)));
    }

    public GemCuttingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<Identifier> location) {
        super(inputs, outputs, location);
    }

    /** Synthetic (non recipe-backed) display id: {@code jewelry:bold_ruby} → {@code jewelry:gem_cutting/bold_ruby}. */
    public static Identifier displayId(Identifier cutId) {
        return Identifier.of(cutId.getNamespace(), NAME + "/" + cutId.getPath());
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public DisplaySerializer<GemCuttingDisplay> getSerializer() {
        return SERIALIZER;
    }
}
