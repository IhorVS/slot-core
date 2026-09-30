# Slot Core

`slot-core` is a library of core slot-machine components. It models reels,
game-field state, winning-combination detection, and prize resolution without
being tied to a particular math model, paytable, configuration file format,
or user interface.

For a runnable example, see [slot-core-demo](https://github.com/IhorVS/slot-core-demo).

The library provides both individual components and `StandardSlotEngine`, which
coordinates a complete spin using a supplied configuration and matcher factory.
Reel positions are supplied by the application; the engine does not generate
random positions.

## Components

### Reel

The `ivs.game.accessories.slot.reel` package defines reel items, reels, and
reel banks. Reels are cyclic sequences of items. A reel bank holds reels in
their left-to-right order.

The `reel.impl` package provides `StandardReelItem`, `StandardReel`, and
`StandardReelBank`.

### Field

The `ivs.game.accessories.slot.field` package defines the game field and
traversal of its items. A field consists of vertical columns, which may have
different heights. Columns are indexed from left to right; rows are indexed
from top to bottom.

The `field.impl` package provides `StandardSlotField` and two builders:

- `ReelBankFieldBuilder` builds a field from a reel bank and one physical
  position per reel.
- `ColumnsFieldBuilder` builds a field from prepared columns.

### Mappers

The `ivs.game.accessories.slot.mapping` packages convert external descriptions
into library objects. Reading YAML, JSON, or another configuration format is
the application's responsibility.

- `mapping.reel` converts ordered string item identifiers and visible reel
  sizes into reels and a reel bank. `StandardReelBankMapper.forNames()` maps
  names such as `"A"` and `"SCT"` to `StandardReelItem`. `forCharacters()`
  accepts character identifiers. Custom item or reel mappers can also be used.
- `mapping.line` converts a list of row indices into a `FieldLine`. Each index
  identifies the row in the corresponding column. A
  `DefaultFieldLineListMapper` assigns line IDs from source list indices,
  starting at `0`.
- `mapping.combination` converts descriptions containing an ID, group ID,
  and ordered string item identifiers into combinations.
  `StandardCombinationListMapper.forNames()` maps standard item names and
  returns combinations indexed by ID. Custom item and combination mappers
  can also be used.

A combination description identifies its group. Match policies use group IDs
when selecting matches. The application decides which combinations to pass
to each matcher.

### Matcher

The `ivs.game.accessories.slot.matcher` package defines combinations,
detected matches, and matchers. A matcher is created for one field state and
returns matches detected on that field.

`AbstractCombinationMatcher` is a base class for implementations.
`CombinationMatcherOrchestrator` coordinates multiple matchers.

### Linear matcher

The `matcher.linear` package contains `FieldLine`,
`LinearCombinationMatcher`, and `LinearCombinationMatch`.

A linear matcher checks combinations from left to right along its configured
lines. It can also accept a set of items that substitute required items after
the first position, including the last position. A line starting with a configured
wild substitute does not match. For example, `A, WLD, WLD` can match `AAA`,
but `WLD, A, A` cannot.

### Scatter matcher

The `matcher.scatter` package contains `ScatterCombinationMatcher` and
`ScatterCombinationMatch`. The matcher looks for required items anywhere
on the field and reports their positions.

### Match policies

The `matcher.policy` package defines how matches from the same group are
selected. Available implementations retain all matches, only the longest
matches, or only the shortest matches.

A policy is supplied when creating a matcher.

### Matcher orchestration

The `matcher.orchestration` package provides two orchestrators:

- `SequentialCombinationMatcherOrchestrator` invokes matchers in the order
  supplied and appends their results in that order.
- `ParallelCombinationMatcherOrchestrator` submits matchers to its supplied
  executor and collects results in the supplied matcher order. Actual parallelism
  depends on the executor; matcher execution order is not guaranteed.

Use the sequential orchestrator when matcher execution order matters.

### Prize resolution

The `ivs.game.accessories.slot.prize` package defines `PrizeResolver` and
provides `StandardPrizeResolver`.

`StandardPrizeResolver` looks up configured prize identifiers for detected
combinations. Its result is a map keyed by combination. If the same
combination matches more than once, its prize identifiers appear once in
that map. The original match list still contains the individual matches.

### Engine

The `ivs.game.accessories.slot.engine` package provides:

- `SlotEngineConfig`: a ready-made reel bank, lines, wild substitutes,
  combinations, and string prize identifiers by combination.
- `CombinationMatcherFactory`: creates matchers from the configuration and the
  field generated for each spin. The factory chooses combination subsets,
  matcher types, and match policies.
- `StandardSlotEngine`: validates the configuration, builds the field, calls the
  factory, runs its matchers sequentially in list order, and resolves prizes.
- `SpinResult`: reel positions as an `int[]`, the generated field, individual
  matches, and prize identifiers by combination.

Reels may have different visible window sizes. The engine requires exactly one
valid physical position per reel and does not generate random positions.

The configuration retains its components and collections by reference. Keep them
unchanged while the engine is in use. Configuration validation checks unique line
and combination IDs, unique ordered combination item sequences, valid line
columns, and prize references and identifiers. Line rows are checked against the
field generated for each spin. Empty configuration collections are allowed;
the factory determines which data its matchers require.

The factory must return a non-null list without null elements. An empty list is
allowed and produces no matches or prizes. A factory contract violation causes
`IllegalStateException` before any matcher runs. Exceptions thrown by the factory
or matchers propagate to the caller.

Results returned by the engine contain unmodifiable match and prize collections,
including nested prize sets. `getReelPositions()` returns a fresh array copy.
Repeated matches share one prize entry without being removed from the match list.

## Assemble a slot from individual components

The following example uses three reels and three lines. All item
identifiers are `StandardReelItem` names. The shown Java fragments belong to
the same method; add imports for the referenced classes.

### 1. Map reel descriptions

A `ReelDescription` contains item names in physical reel order and the number
of items visible on that reel. Descriptions are listed from left to right.

```java
List<ReelDescription> reelDescriptions = List.of(
        new ReelDescription(List.of("A", "SCT", "K"), 3),
        new ReelDescription(List.of("A", "SCT", "Q"), 3),
        new ReelDescription(List.of("A", "SCT", "J"), 3)
);

ReelBank<StandardReelItem> reelBank =
        StandardReelBankMapper.forNames().map(reelDescriptions);
```

### 2. Map line descriptions

Each number is the row to inspect in the corresponding column. The mapper
assigns ID `0` to the first line, ID `1` to the second, and so on.

```java
List<FieldLine> lines = new DefaultFieldLineListMapper(
        new DefaultFieldLineMapper()
).map(List.of(
        List.of(0, 0, 0),
        List.of(0, 1, 2),
        List.of(2, 1, 0)
));
```

Line `0` is horizontal; lines `1` and `2` are diagonals.

### 3. Map combination descriptions

IDs identify individual combinations. Group IDs determine which combinations
a match policy compares with one another.

```java
Map<Integer, Combination<StandardReelItem>> combinations =
        StandardCombinationListMapper.forNames().map(List.of(
                new CombinationDescription(0, "A", List.of("A", "A", "A")),
                new CombinationDescription(
                        1, "SCT", List.of("SCT", "SCT", "SCT")
                )
        ));

List<Combination<StandardReelItem>> linearCombinations =
        List.of(combinations.get(0));

List<Combination<StandardReelItem>> scatterCombinations =
        List.of(combinations.get(1));
```

The application chooses which mapped combinations belong to each matcher.

### 4. Configure prizes

Prizes are associated with combinations. The identifiers can be strings or
another application-defined type.

```java
StandardPrizeResolver<StandardReelItem, String> prizeResolver =
        new StandardPrizeResolver<>(Map.of(
                combinations.get(0), Set.of("C10"),
                combinations.get(1), Set.of("C15")
        ));
```

### 5. Build the field for a spin

Supply one physical position for each reel, from left to right. This example
uses position `0` on every reel.

```java
ReelBankFieldBuilder<StandardReelItem> fieldBuilder =
        new ReelBankFieldBuilder<>(reelBank);

SlotField<StandardReelItem> field = fieldBuilder.build(0, 0, 0);
```

For another spin, build another field using its reel positions.

### What the example builds

The three reel descriptions define these cyclic reels. Each reel displays
three items, starting at its selected physical position:

```text
Reel 0       Reel 1       Reel 2
-------      -------      -------
0: A         0: A         0: A
1: SCT       1: SCT       1: SCT
2: K         2: Q         2: J
```

With positions `(0, 0, 0)`, the field builder produces:

```text
             Reel 0   Reel 1   Reel 2
             ------   ------   ------
Row 0           A        A        A
Row 1          SCT      SCT      SCT
Row 2           K        Q        J
```

Each row index selects an item in its corresponding column. The three lines
pass through the field as follows:

| Line ID | Row indices | Items from left to right |
|---------|-------------|--------------------------|
| `0`     | `[0, 0, 0]` | `A → A → A`              |
| `1`     | `[0, 1, 2]` | `A → SCT → J`            |
| `2`     | `[2, 1, 0]` | `K → SCT → A`            |

The following diagrams show each line on the field. `*` marks a position
on the line; `·` marks every other position.

Line `0` — `[0, 0, 0]`:

```text
          Reel 0  Reel 1  Reel 2
Row 0       *       *       *
Row 1       ·       ·       ·
Row 2       ·       ·       ·
```

Line `1` — `[0, 1, 2]`:

```text
          Reel 0  Reel 1  Reel 2
Row 0       *       ·       ·
Row 1       ·       *       ·
Row 2       ·       ·       *
```

Line `2` — `[2, 1, 0]`:

```text
          Reel 0  Reel 1  Reel 2
Row 0       ·       ·       *
Row 1       ·       *       ·
Row 2       *       ·       ·
```

The example contains two combinations:

| ID | Matcher | Group | Required items   | Where it matches              | Prize |
|----|---------|-------|------------------|-------------------------------|-------|
| 0  | Linear  | `A`   | `A, A, A`        | Line `0`, from left to right  | `C10` |
| 1  | Scatter | `SCT` | `SCT, SCT, SCT`  | Anywhere on the field         | `C15` |

Both combinations match this field. Of the three lines, only line `0`
matches the linear combination. Its match contains positions
`(0, 0)`, `(1, 0)`, and `(2, 0)`, where each pair is `(column, row)`.
The scatter match contains `(0, 1)`, `(1, 1)`, and `(2, 1)`.

The sequential orchestrator checks lines `0`, `1`, and `2` in order, then
checks scatters. It returns the line `0` match before the scatter match.
Prize resolution then associates
combination `0` with `C10` and combination `1` with `C15`.

### 6. Create matchers for that field

Matchers receive the field state in their constructors. Create new matcher
instances for each spin. A separate linear matcher can be created for each
line when matches must be processed line by line.

```java
List<CombinationMatcher<
        StandardReelItem,
        ? extends CombinationMatch<StandardReelItem>>> matchers = new ArrayList<>();

for (FieldLine line : lines) {
    matchers.add(new LinearCombinationMatcher<>(
            field,
            List.of(line),
            linearCombinations,
            Set.of(StandardReelItem.WLD),
            new LongestCombinationMatchPolicy<>()
    ));
}

matchers.add(new ScatterCombinationMatcher<>(
        field,
        scatterCombinations,
        new LongestCombinationMatchPolicy<>()
));
```

### 7. Run matchers and resolve prizes

The sequential orchestrator invokes the three linear matchers in line order,
then the scatter matcher. Its result preserves individual matches, including
matches of the same combination on different lines.

```java
SequentialCombinationMatcherOrchestrator<StandardReelItem> orchestrator =
        new SequentialCombinationMatcherOrchestrator<>();

List<CombinationMatch<StandardReelItem>> matches =
        orchestrator.match(matchers);

Map<Combination<StandardReelItem>, Set<String>> prizes =
        prizeResolver.resolve(matches);
```

The application can now use `field`, `matches`, and `prizes` to form its spin
result or display it. To associate prizes with each individual match, look up
`prizes.get(match.combination())` while iterating over `matches`.

For subsequent spins, reuse the configured reel bank, lines, combinations,
field builder, and prize resolver. Build a new field and create matchers for
that field before invoking the orchestrator again.


## Perform spins with the standard engine

Reuse the reel bank, lines, and combinations mapped in steps 1–3 above.
Instead of building fields, creating matchers, and resolving prizes manually,
put the static data into an engine configuration:

```java
SlotEngineConfig<StandardReelItem> config = new SlotEngineConfig<>(
        reelBank,
        lines,
        Set.of(StandardReelItem.WLD),
        List.of(combinations.get(0), combinations.get(1)),
        Map.of(
                combinations.get(0), Set.of("C10"),
                combinations.get(1), Set.of("C15")
        )
);
```

Provide a factory that selects combination groups and creates matchers for the
supplied field. In this example, group `A` is linear and group `SCT` is scatter.
These are application choices, not group names reserved by the library.

```java
CombinationMatcherFactory<StandardReelItem> matcherFactory = (engineConfig, spinField) -> {
    List<Combination<StandardReelItem>> linear = engineConfig.getCombinations().stream()
            .filter(combination -> combination.getGroupId().equals("A"))
            .toList();

    List<Combination<StandardReelItem>> scatters = engineConfig.getCombinations().stream()
            .filter(combination -> combination.getGroupId().equals("SCT"))
            .toList();

    var linearMatcher = new LinearCombinationMatcher<>(
            spinField,
            engineConfig.getLines(),
            linear,
            engineConfig.getWildSubstitutes(),
            new LongestCombinationMatchPolicy<>()
    );

    var scatterMatcher = new ScatterCombinationMatcher<>(
            spinField,
            scatters,
            new LongestCombinationMatchPolicy<>()
    );

    return List.of(linearMatcher, scatterMatcher);
};

StandardSlotEngine<StandardReelItem> engine = new StandardSlotEngine<>(config, matcherFactory);
```

This factory creates one linear matcher for all three lines, followed by one
scatter matcher. The example configuration contains both combination groups;
a factory supporting optional groups should omit matchers whose required data
is absent.

Perform a spin with explicitly supplied positions:

```java
SpinResult<StandardReelItem> result = engine.spin(0, 0, 0);

int[] reelPositions = result.getReelPositions();
SlotField<StandardReelItem> spinField = result.getField();
List<CombinationMatch<StandardReelItem>> spinMatches = result.getMatches();
Map<Combination<StandardReelItem>, Set<String>> spinPrizes = result.getPrizesByCombination();
```

The result contains the same field, linear match, scatter match, and prizes shown
in the diagrams above. For prizes associated with an individual match, use
`spinPrizes.getOrDefault(match.combination(), Set.of())`.

Reuse the engine for subsequent spins. It builds a new field and calls the
factory on every invocation. Random position selection, user interaction, and
prize interpretation belong to the application.
