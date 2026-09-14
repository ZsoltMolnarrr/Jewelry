package net.jewelry.api.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.jewelry.api.GemCutBuilder;
import net.jewelry.gems.GemCut;
import net.jewelry.gems.GemCutRegistry;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureMap;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.RegistryWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/// Datagen provider for gem cuts — subclass it in your Fabric datagen entrypoint and add the cuts built
/// with [GemCutBuilder]; each becomes `data/<ns>/gem_cut/<path>.json`.
///
/// ```java
/// public class MyGemCuts extends GemCutGenerator {
///     public MyGemCuts(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
///         super(output, registries);
///     }
///     @Override protected void generate(Entries entries) { MyCuts.all.forEach(entries::add); }
/// }
/// ```
///
/// Writes the JSON itself rather than through `FabricDynamicRegistryProvider`, because a cut with a required
/// mod carries load conditions for BOTH loaders (`fabric:load_conditions` and `neoforge:conditions`), and
/// the Fabric provider only knows its own. Each loader ignores the other's field.
///
/// Item models are a separate concern of your model provider: call [#generateDefaultGemModel] per raw gem
/// (the sprite all its cuts share) and [#generateItemModel] per cut that opted into a custom icon.
public abstract class GemCutGenerator implements DataProvider {
    public interface Entries {
        void add(GemCutBuilder.Entry entry);
    }

    private final FabricDataOutput output;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;

    public GemCutGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        this.output = output;
        this.registriesFuture = registriesFuture;
    }

    protected abstract void generate(Entries entries);

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return registriesFuture.thenCompose(registries -> {
            var ops = registries.getOps(JsonOps.INSTANCE);
            var resolver = output.getResolver(DataOutput.OutputType.DATA_PACK, GemCutRegistry.ID.getPath());
            List<GemCutBuilder.Entry> cuts = new ArrayList<>();
            generate(cuts::add);
            List<CompletableFuture<?>> writes = new ArrayList<>();
            for (var cut : cuts) {
                var json = GemCut.CODEC.encodeStart(ops, cut.definition())
                        .getOrThrow(message -> new IllegalStateException("Failed to encode gem cut " + cut.id() + ": " + message))
                        .getAsJsonObject();
                if (cut.requiredMod() != null) {
                    addLoadConditions(json, cut.requiredMod());
                }
                writes.add(DataProvider.writeToPath(writer, json, resolver.resolveJson(cut.id())));
            }
            return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
        });
    }

    /// The gem's default cut model, `<ns>:item/gem_cut/<gem path>`, textured from the same path
    /// (`textures/item/gem_cut/<gem path>.png`). Every raw gem that has cuts should emit one.
    public static void generateDefaultGemModel(ItemModelGenerator itemModelGenerator, ItemConvertible gem) {
        var model = GemCut.defaultModelId(gem.asItem());
        Models.GENERATED.upload(model, TextureMap.layer0(model), itemModelGenerator.writer);
    }

    /// Flat "generated" item model for a cut's custom icon, textured from the same path
    /// (`jewelry:item/gem_cut/bold_ruby` → `textures/item/gem_cut/bold_ruby.png`). No-op for cuts
    /// without a custom icon.
    public static void generateItemModel(ItemModelGenerator itemModelGenerator, GemCutBuilder.Entry cut) {
        cut.model().ifPresent(model ->
                Models.GENERATED.upload(model, TextureMap.layer0(model), itemModelGenerator.writer));
    }

    /// The same condition pair Jewelry's Lithostitched files use.
    private static void addLoadConditions(JsonObject json, String modId) {
        var fabricCondition = new JsonObject();
        fabricCondition.addProperty("condition", "fabric:all_mods_loaded");
        var values = new JsonArray();
        values.add(modId);
        fabricCondition.add("values", values);
        var fabricConditions = new JsonArray();
        fabricConditions.add(fabricCondition);
        json.add("fabric:load_conditions", fabricConditions);

        var neoforgeCondition = new JsonObject();
        neoforgeCondition.addProperty("type", "neoforge:mod_loaded");
        neoforgeCondition.addProperty("modid", modId);
        var neoforgeConditions = new JsonArray();
        neoforgeConditions.add(neoforgeCondition);
        json.add("neoforge:conditions", neoforgeConditions);
    }

    @Override
    public String getName() {
        return "Gem Cuts";
    }
}
