package net.jewelry.api.bonus;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/// The (unsynced, code-side) registry of gem bonus types. Not a vanilla registry on purpose: no per-loader
/// registry plumbing, no freeze timing — just a map filled at mod init.
///
/// Register your types from your mod initializer, on BOTH sides: gem cuts are synced to clients and decoded
/// there with the same codec, so a client lacking the mod that defines a type cannot join. A cut using an
/// unknown type fails to load like any malformed registry entry (the world does not start), so a cut whose
/// type belongs to an optional mod must carry that mod's load conditions.
///
/// ```java
/// public static final GemBonusType<StatusEffectBonus> STATUS_EFFECT =
///         GemBonusTypes.register("mymod:status_effect", StatusEffectBonus.CODEC);
/// ```
public class GemBonusTypes {
    public static final String TYPE_FIELD = "type";
    public static final String DEFAULT = "attribute";

    private static final Map<String, GemBonusType<?>> types = new LinkedHashMap<>();

    /// Jewelry's own kind: one attribute modifier. The default when a cut's bonus has no `type`.
    public static final GemBonusType<AttributeBonus> ATTRIBUTE = register(DEFAULT, AttributeBonus.CODEC);

    public static synchronized <T extends GemBonus> GemBonusType<T> register(String id, MapCodec<T> codec) {
        if (types.containsKey(id)) {
            throw new IllegalStateException("Gem bonus type '" + id + "' is already registered");
        }
        var type = new GemBonusType<>(id, codec);
        types.put(id, type);
        return type;
    }

    public static synchronized Optional<GemBonusType<?>> get(String id) {
        return Optional.ofNullable(types.get(id));
    }

    public static synchronized Map<String, GemBonusType<?>> all() {
        return Map.copyOf(types);
    }

    /// `{ "type": "<id>", …that type's fields… }`, `type` defaulting to [#DEFAULT] (and omitted when encoding
    /// the default kind). A hand-rolled dispatch because DFU's `dispatchMap` has no notion of a default key.
    public static final MapCodec<GemBonus> CODEC = new MapCodec<>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString(TYPE_FIELD));
        }

        @Override
        public <T> DataResult<GemBonus> decode(DynamicOps<T> ops, MapLike<T> input) {
            var typeValue = input.get(TYPE_FIELD);
            var typeId = typeValue == null
                    ? DataResult.success(DEFAULT)
                    : ops.getStringValue(typeValue);
            return typeId.flatMap(id -> get(id)
                    .map(type -> type.codec().decode(ops, input).map(bonus -> (GemBonus) bonus))
                    .orElseGet(() -> DataResult.error(() -> "Unknown gem bonus type '" + id + "'")));
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> RecordBuilder<T> encode(GemBonus input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            var type = (GemBonusType<GemBonus>) input.type();
            if (!type.id().equals(DEFAULT)) {
                prefix.add(TYPE_FIELD, ops.createString(type.id()));
            }
            return type.codec().encode(input, ops, prefix);
        }

        @Override
        public String toString() {
            return "GemBonus";
        }
    };
}
