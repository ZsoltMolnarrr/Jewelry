package net.jewelry.api.bonus;

import com.mojang.serialization.MapCodec;
import net.minecraft.text.Text;

/// What a gem cut grants. Jewelry ships one kind, [AttributeBonus]; other mods register their own kinds
/// with [GemBonusTypes] and give them any meaning they like (a spell modifier, a status effect, …).
///
/// The framework asks nothing of a bonus but its [#type()] (to serialise it) and a [#description()] (the
/// line shown on the cut gem's tooltip and on the socket line of the item holding it). Making a custom
/// bonus DO something is entirely the registering mod's job: collect the socketed bonuses of your type
/// with `GemSockets.bonuses(stack, MyBonus.class)` from whatever hook fits the mechanic.
public interface GemBonus {
    /// Codec of a bonus object: `{ "type": "<id>", …fields of that type… }`; `type` defaults to
    /// `attribute` when absent, so the plain attribute cuts need no type field at all.
    MapCodec<GemBonus> CODEC = GemBonusTypes.CODEC;

    GemBonusType<?> type();

    /// One line describing the bonus, styled — e.g. "+4% Attack Damage" in blue.
    Text description();
}
