package net.jewelry.api.bonus;

import com.mojang.serialization.MapCodec;

/// A kind of gem bonus: its `type` id in cut JSON and the codec of its fields. Obtain one from
/// [GemBonusTypes#register].
///
/// The id is a plain string (`attribute`, or something like `mymod:status_effect` — prefixing with your
/// mod id is the polite way to avoid clashes; the registry is a single flat namespace).
public record GemBonusType<T extends GemBonus>(String id, MapCodec<T> codec) {
}
