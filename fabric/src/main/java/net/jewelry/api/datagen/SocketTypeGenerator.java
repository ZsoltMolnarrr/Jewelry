package net.jewelry.api.datagen;

import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.jewelry.api.SocketTypeBuilder;
import net.jewelry.gems.SocketType;
import net.jewelry.gems.SocketTypeRegistry;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/// Datagen provider for socket type definitions: subclass it and add the types built with
/// [SocketTypeBuilder]; each becomes `data/<ns>/socket_type/<path>.json`.
public abstract class SocketTypeGenerator implements DataProvider {
    public interface Entries {
        void add(SocketTypeBuilder.Entry entry);
    }

    private final FabricDataOutput output;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;

    public SocketTypeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        this.output = output;
        this.registriesFuture = registriesFuture;
    }

    protected abstract void generate(Entries entries);

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return registriesFuture.thenCompose(registries -> {
            var ops = registries.getOps(JsonOps.INSTANCE);
            var resolver = output.getResolver(DataOutput.OutputType.DATA_PACK, SocketTypeRegistry.ID.getPath());
            List<SocketTypeBuilder.Entry> types = new ArrayList<>();
            generate(types::add);
            List<CompletableFuture<?>> writes = new ArrayList<>();
            for (var type : types) {
                var json = SocketType.CODEC.encodeStart(ops, type.definition())
                        .getOrThrow(message -> new IllegalStateException("Failed to encode socket type " + type.id() + ": " + message));
                writes.add(DataProvider.writeToPath(writer, json, resolver.resolveJson(type.id())));
            }
            return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Socket Types";
    }
}
