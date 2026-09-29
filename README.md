![Title](.github/title.png)

### 💍 Find precious gems deep down, and craft them into powerful jewelry!

This is a content support mod for RPG Series content mods.

No wiki is maintained at this moment, as all the information is available in-game.

### 💎 Gem cuts & sockets

Raw gems are cut at the Jeweler's Kit into cut gems that are inserted into equipment sockets at an anvil.
A Socket Mount adds a socket to a piece of armor at an anvil; a grindstone takes mounted sockets off again
once the item has no enchantments left to remove (gems in them are lost, the item's own sockets stay).

Cuts are data (`data/<ns>/gem_cut/*.json`): data packs add their own, see [Adding gem cuts with a data pack](#adding-gem-cuts-with-a-data-pack); mods can author them in datagen, see [Adding gem cuts from another mod](#adding-gem-cuts-from-another-mod). Sockets that only take certain gems: [Advanced use-cases](#advanced-use-cases).

For under the hood technical details, check out the projects providing the custom attributes:
- [Spell Power Attributes](https://github.com/ZsoltMolnarrr/SpellPower)
- [Ranged Weapon API](https://github.com/FabricExtras/RangedWeaponAPI)
- [Combat Roll](https://github.com/ZsoltMolnarrr/CombatRoll)

### ⚙️ Feature flags

`config/jewelry/features.json` can switch either half off, e.g. when another mod provides a similar system.

| Flag | Default | Read by | Effect when `false` |
|---|---|---|---|
| `gem_cuts` | `true` | the server (the integrated one in singleplayer), on every datapack load, so `/reload` applies it | No gem cut data is loaded, from Jewelry or any pack. The synced registry reaches clients empty, so nothing of the system shows: no cuts in the Gems tab, an inert Jeweler's Kit, no socket lines, no REI category. Cut gems already in the world are lost. |
| `sockets` | `true` | each side from its own file, at runtime; set it on server and clients alike | No item resolves sockets: no socket tooltip lines, socketed gems grant nothing, the anvil won't socket. Item data is untouched, so turning it back on restores everything. |

# Adding gem cuts with a data pack

Every gem cut is one JSON file in a data pack, no code needed. Jewelry's own cuts live at
`data/jewelry/gem_cut/<name>.json`; put yours at `data/<your_namespace>/gem_cut/<name>.json`.

```json
{
  "gem": "jewelry:ruby",
  "bonus": {
    "attribute": "minecraft:attack_damage",
    "operation": "add_multiplied_base",
    "value": 0.04
  },
  "color": "#E5404F"
}
```

| Field | Meaning |
|---|---|
| `gem` | The raw gem item this cut applies to. Jewelry's: `jewelry:ruby`, `sapphire`, `jade`, `topaz`, `citrine`, `tanzanite`. |
| `bonus` | What the cut grants. Its `type` picks the kind of bonus and defaults to `attribute`, the only kind Jewelry ships; mods can register further kinds (see "Custom bonus kinds" below). |
| `bonus.attribute` | One attribute id, e.g. `minecraft:max_health`, `spell_power:fire`, `ranged_weapon:damage`. Unknown ids load fine and grant nothing. |
| `bonus.operation` | `add_value` (flat), `add_multiplied_base` (percent of base, `0.04` = +4 %) or `add_multiplied_total`. |
| `bonus.value` | The bonus amount. |
| `color` | Tint of the socket glyph on equipment tooltips, `#RRGGBB`. |
| `model` | Optional. A custom item model for this cut, e.g. `mypack:item/gem_cut/bold_ruby`. Without it the cut uses its gem's default cut sprite. |

**Name.** Add `"gem_cut.<your_namespace>.<name>": "Bold Ruby"` to a lang file in a resource pack
(`assets/<your_namespace>/lang/en_us.json`). The whole name, e.g. "Bold Ruby", not just the prefix.

**Custom icon (optional).** In a resource pack, add the model `assets/<your_namespace>/models/item/gem_cut/<name>.json`
(a normal `minecraft:item/generated` model) with its texture, plus its item-model definition
`assets/<your_namespace>/items/gem_cut/<name>.json` (`{ "model": { "type": "minecraft:model", "model": "<your_namespace>:item/gem_cut/<name>" } }`),
and reference the MODEL from the cut's `model` field (`<your_namespace>:item/gem_cut/<name>`).

**Only with another mod installed.** Add both loaders' conditions to the cut; each loader ignores the other's:

```json
"fabric:load_conditions": [ { "condition": "fabric:all_mods_loaded", "values": [ "critical_strike" ] } ],
"neoforge:conditions": [ { "type": "neoforge:mod_loaded", "modid": "critical_strike" } ]
```

The cut then appears in the Gems creative tab, in the Jeweler's Kit for its gem, and in REI. The server log
lists the loaded cuts at startup, handy for checking a pack.

# Adding gem cuts from another mod

Gem cuts are data: one JSON per cut in the synced `gem_cut` registry at `data/<ns>/gem_cut/<path>.json`.
Any mod (or data pack) can add cuts for Jewelry's gems or for its own. Jewelry offers a Java API to author
them in datagen; nothing at runtime needs to know about your mod.

## Dependencies

- Runtime / common compile: `net.rpg_series:jewelry-fabric:<version>` (the ecosystem pattern — `common`
  compiles against the Fabric artifact; NeoForge gets `jewelry-neoforge` at runtime).
- Fabric datagen: the same `jewelry-fabric` artifact contains `net.jewelry.api.datagen.GemCutGenerator`.

## 1. Build the cuts (common)

```java
public class MyGemCuts {
    public static final List<GemCutBuilder.Entry> all = new ArrayList<>();

    public static final GemCutBuilder.Entry BOLD_GARNET = add(
            GemCutBuilder.create(Identifier.of("mymod", "bold_garnet"), MyItems.GARNET)
                    .multiplyBase(Identifier.ofVanilla("attack_damage"), 0.05F)
                    .color(0xB0303A));

    public static final GemCutBuilder.Entry KEEN_GARNET = add(
            GemCutBuilder.create(Identifier.of("mymod", "keen_garnet"), MyItems.GARNET)
                    .multiplyBase(Identifier.of("critical_strike", "chance"), 0.04F)
                    .requiresMod("critical_strike"));   // load conditions for both loaders

    private static GemCutBuilder.Entry add(GemCutBuilder builder) { var e = builder.build(); all.add(e); return e; }
}
```

`GemCutBuilder`:
- `create(id, gem)` — the RAW gem item this cut applies to (Jewelry's `Gems.ruby.item()` or your own).
- exactly one bonus: `multiplyBase(attribute, value)`, `addValue(attribute, value)` or
  `attribute(attribute, value, operation)` for an attribute modifier — the attribute is an id, so a missing
  mod's attribute means "no bonus", never a load failure; or `bonus(GemBonus)` for a kind of your own (see
  "Custom bonus kinds" below).
- `color(rgb)` — tint of the socket glyph on equipment tooltips. The cut's name is never tinted.
- `customIcon()` — opt in to a sprite of the cut's own (`textures/item/gem_cut/<path>.png`). Without it
  every cut of a gem shares that gem's default cut sprite (`textures/item/gem_cut/<gem path>.png`).
  `model(id)` sets an explicit model for anything the convention doesn't cover.
- `requiresMod(modId)` — the cut only loads with that mod present.

## 2. Generate (Fabric datagen)

```java
pack.addProvider(MyGemCutGenerator::new);

public static class MyGemCutGenerator extends GemCutGenerator {
    public MyGemCutGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(output, registries);
    }
    @Override
    protected void generate(Entries entries) {
        MyGemCuts.all.forEach(entries::add);
    }
}

// in your FabricModelProvider#generateItemModels:
GemCutGenerator.generateDefaultGemModel(itemModelGenerator, MyItems.GARNET);           // per raw gem
MyGemCuts.all.forEach(cut -> GemCutGenerator.generateItemModel(itemModelGenerator, cut)); // per custom icon
```

## 3. Resources

| What | Where |
|---|---|
| Default cut sprite | `assets/<ns>/textures/item/gem_cut/<gem path>.png` (16×16), one per raw gem, shared by its cuts |
| Custom icon (opt-in) | `assets/<ns>/textures/item/gem_cut/<cut path>.png`, only for cuts built with `customIcon()` |
| Name | lang key `gem_cut.<ns>.<path>` — the WHOLE name ("Bold Garnet"), no runtime prefix composition |
| Models | generated by `generateDefaultGemModel` / `generateItemModel`; their item-model definitions (`items/gem_cut/*.json`) are written by `GemCutGenerator` and discovered by the game itself |

## Your own raw gem

Make its item extend `net.jewelry.gems.GemItem`: that gives the cut gem its name, colour-free, and the bonus
line in its tooltip. Cuts for any gem show in Jewelry's Gems creative tab (Jewelry's gems first) and in the
Jeweler's Kit when that gem is placed in it.

## Giving your items sockets

Sockets are the `jewelry:sockets` component: one entry per socket, e.g. `[{}]` for one empty socket,
`[{}, {}]` for two. Set it as a default component on the item and the socket system does the rest
(tooltip, anvil socketing, gem bonuses).

With Jewelry on the compile classpath:

```java
new Item.Settings().component(GemComponents.SOCKETS, SocketsComponent.empty(1))
```

**Without depending on Jewelry** — resolve the component type by id and decode the value with its own codec,
so your mod only needs to know the id and the JSON shape:

```java
if (isModLoaded("jewelry")) {   // FabricLoader.getInstance().isModLoaded / ModList.get().isLoaded
    var type = Registries.DATA_COMPONENT_TYPE.get(Identifier.of("jewelry", "sockets"));
    var value = type.getCodec().parse(JsonOps.INSTANCE, JsonParser.parseString("[{}]")).getOrThrow();
    settings.component((ComponentType<Object>) type, value);   // unchecked cast, deliberately
}
```

This works at item-construction time on both loaders regardless of mod load order, because Jewelry registers
its component types during vanilla's component bootstrap (`DataComponentTypes`), before any mod registers items.

## Runtime pieces you may want

- `GemComponents.CUT` — the cut on a gem stack; `GemCut.of(stack)` / `GemCut.stack(entry)`.
- `GemComponents.SOCKETS` + `SocketsComponent.empty(n)` — give your equipment sockets as a default
  component: `new Item.Settings().component(GemComponents.SOCKETS, SocketsComponent.empty(1))`.
- `GemComponents.ITEM_MODEL` — explicit per-stack model, the SpellEngine `item_model` replica.
- `GemSockets.bonuses(stack)` / `bonuses(stack, MyBonus.class)` — the socketed gems of a stack with their
  bonuses; the discovery helper for custom bonus kinds.

# Advanced use-cases

## Socket types: sockets that only take certain gems

By default every socket is a standard socket and every cut fits it, so nothing in the guides above needs any
of this. A mod that brings its own family of gems — and wants them kept apart from the standard ones, both
ways — gives its sockets a **type**.

A socket's `type` is an id on the socket entry:

```
jewelry:sockets=[ {}, {"type": "jewelry:mounted"}, {"type": "witcher:sign"} ]
```

- `{}` — a plain standard socket.
- `{"type": "jewelry:mounted"}` — written by the Socket Mount so its one-per-item cap can be counted. Its definition
  only restyles it (dim gold "Empty Mounted Socket", brighter gold frame once filled); with no `accepts` it is still a standard socket. A type with no
  definition at all works the same, with the generic look.
- `{"type": "witcher:sign"}` — a type that has a **definition**, below.

### Defining a type

`data/<ns>/socket_type/<path>.json`, in the synced `socket_type` registry. Every field is optional:

```json
{
  "accepts": "#witcher:sign_gems",
  "title": { "translate": "socket.witcher.sign" },
  "empty": { "icon": "\uDB80\uDD10", "color": "#4E8F84" },
  "fill":  { "icon": "\uDB80\uDD11", "color": "#7FD4C1" }
}
```

| Field | Meaning |
|---|---|
| `accepts` | A tag of gem cuts (`data/<ns>/tags/gem_cut/<path>.json`). The socket takes those cuts and nothing else. Absent → a standard socket. |
| `title` | Text of the empty socket, e.g. "Empty Sign Socket". Absent → "Empty Socket". |
| `empty` | Appearance while empty: `icon` and `color`, both optional. The colour applies to the whole line. Absent → Jewelry's frame, dark gray. |
| `fill` | Appearance while holding a gem: the same two fields. The colour applies to the frame only; the gem is tinted by its cut and the bonus text stays vanilla blue. Absent → Jewelry's frame, gray. |

An `icon` is a character your resource pack maps to a bitmap in `assets/minecraft/font/default.json`. Keep it
9×9 like Jewelry's so the gem drawn over a filled socket lines up.

### Keeping your gems out of standard sockets

`#jewelry:restricted` is the blacklist of standard sockets: a cut in it fits only sockets whose type `accepts`
it. Tags can include tags, so it is one line:

```json
// data/jewelry/tags/gem_cut/restricted.json
{ "values": [ "#witcher:sign_gems" ] }
```

The other direction needs nothing: standard cuts are not in your tag, so your sockets refuse them.

### What follows

- A gem goes into the first empty socket that accepts it. When none is empty, only the accepting sockets are
  reset to the new gem — a gem of one family never wipes another family's gems. An item with no accepting
  socket gives no anvil result.
- A Socket Mount adds sockets of the `type` its `jewelry:socket_mount` component names, capped per type by
  `max_mounted`. Name your defined type there to ship a mount for your own sockets.
- Item defaults: `SocketsComponent.empty(1, Identifier.of("witcher", "sign"))`, or without depending on
  Jewelry, the JSON `[{"type": "witcher:sign"}]` through the lookup shown in *Giving your items sockets*.

### From Java (datagen)

```java
// common
public static final TagKey<GemCut> SIGN_GEMS = SocketTypeBuilder.cutTag(Identifier.of("witcher", "sign_gems"));
public static final SocketTypeBuilder.Entry SIGN = SocketTypeBuilder.create(Identifier.of("witcher", "sign"))
        .accepts(SIGN_GEMS).title(Text.translatable("socket.witcher.sign"))
        .empty("\uDB80\uDD10", 0x4E8F84).fill("\uDB80\uDD11", 0x7FD4C1)
        .build();

// fabric datagen
public static class MySocketTypes extends SocketTypeGenerator {      // → data/witcher/socket_type/sign.json
    ...constructor...
    @Override protected void generate(Entries entries) { entries.add(SIGN); }
}
public static class MyCutTags extends GemCutTagGenerator {           // → the tag + the blacklist entry
    ...constructor...
    @Override protected void configure(RegistryWrapper.WrapperLookup lookup) {
        var tag = builder(SIGN_GEMS);
        MyCuts.all.forEach(cut -> tag.addOptional(RegistryKey.of(GemCutRegistry.KEY, cut.id())));
        restrict(SIGN_GEMS);
    }
}
```

### Sockets the grindstone can take off

Sockets whose type is in the socket type tag `#jewelry:grindstone_removable`
(`data/<ns>/tags/socket_type/grindstone_removable.json`) come off when the item is ground alone and has no
enchantments left to grind (an enchanted item is disenchanted first, as vanilla does; the sockets come off
on the next grind): those sockets and the gems in them are gone, every other socket stays. Jewelry tags `jewelry:mounted`, the type its Socket Mount adds. A mod adding
its own mount type tags it the same way; only types with a definition can be tagged. From datagen:
`SocketTypeTagGenerator` with `grindstoneRemovable(type)`.

## Custom bonus kinds: cuts that grant something other than an attribute

A cut's `bonus` is one object whose `type` names a *bonus kind*. Jewelry ships one kind, `attribute` (the default when `type` is absent), serving as default.

Any mod can register further kinds — a small spell modifier, a status effect while worn, whatever.

⚠️Important note: Jewelry only serves the structural backbone, does not provide any dynamics. So gems with custom bonus types need to be checked by developer to execute their related effect.

### Registering a kind

The kind registry is plain code, not a vanilla registry — `net.jewelry.api.bonus.GemBonusTypes`. Register from your mod initializer, on both sides (cuts are synced and decoded on the client with the same codec):

```java
public record StatusEffectBonus(RegistryEntry<StatusEffect> effect, int amplifier) implements GemBonus {
    public static final MapCodec<StatusEffectBonus> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Registries.STATUS_EFFECT.getEntryCodec().fieldOf("effect").forGetter(StatusEffectBonus::effect),
            Codec.INT.optionalFieldOf("amplifier", 0).forGetter(StatusEffectBonus::amplifier)
    ).apply(i, StatusEffectBonus::new));

    public static final GemBonusType<StatusEffectBonus> TYPE =
            GemBonusTypes.register("mymod:status_effect", CODEC);   // a plain string; prefix it with your mod id

    @Override public GemBonusType<?> type() { return TYPE; }

    /// The line on the cut gem's tooltip and on the socket line of the item holding it.
    @Override public Text description() {
        return Text.translatable("gem_bonus.mymod.status_effect", effect.value().getName()).formatted(Formatting.BLUE);
    }
}
```

The cut JSON then reads:

```json
{
  "gem": "jewelry:ruby",
  "bonus": { "type": "mymod:status_effect", "effect": "minecraft:speed" },
  "color": "#E5404F"
}
```

From Java, build it with `GemCutBuilder.create(id, gem).bonus(new StatusEffectBonus(...))` and generate it
like any other cut.

### Making it do something

`GemSockets.bonuses(stack, StatusEffectBonus.class)` returns the socketed gems of that kind on a stack, in
socket order, each as a `SocketedBonus(socketIndex, cut, bonus)`. Where you call it is up to the mechanic —
an entity tick, an equipment-change hook, a spell-cast event. `SocketedBonus.placementId(slot)` gives an id
unique to that gem's placement (`<ns>:gem_cut/<cut>/<slot>/<index>`) for anything that must not collapse
when the same cut sits in two sockets. Jewelry's own attribute path is written on the same helper.

```java
// e.g. once per second on the server
for (var slot : EquipmentSlot.values()) {
    var stack = entity.getEquippedStack(slot);
    if (!GemSockets.appliesTo(stack, slot)) continue;            // the slot rule Jewelry uses for attributes
    for (var socketed : GemSockets.bonuses(stack, StatusEffectBonus.class)) {
        entity.addStatusEffect(new StatusEffectInstance(socketed.bonus().effect(), 40, socketed.bonus().amplifier()));
    }
}
```

The helper returns nothing while sockets are disabled in `config/jewelry/features.json`, so the feature
flag covers your kind as well.

### Rules

- **Register at init.** Kinds must exist before a world loads its data packs; a kind registered later is
  unknown to cuts that already loaded.
- **An unknown `type` fails the cut** — and, as with every data-pack registry, a failed entry stops the
  world from loading, naming the cut and the unknown type in the log. A cut whose kind belongs to an
  optional mod therefore carries that mod's load conditions (`requiresMod` in Java, or the two condition
  fields in JSON), exactly like a cut for an optional mod's attribute.
- **Clients need the mod** that defines the kind, since cuts are synced and decoded with the kind's codec.
- **One bonus per cut.** A cut grants exactly one bonus; two stats means two cuts.
