# Traincraft Fabric 26.3 Port Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Port the Traincraft mod from its Forge 1.12.2 rewrite (branch `master`, mod version 4.4.1) to Minecraft 26.3 on Fabric, with behavior matching the original 1.7.10 mod wherever the Forge rewrite has it.

**Architecture:** Multi-module Gradle project. `common/` holds pure logic (fuel rules, steam state, registry name lists) with no Minecraft or Fabric imports. `fabric/` is the adapter: registries, block/entity classes, menus, networking, rendering, and gametests. Each phase produces a jar that loads in Prism and passes the headless gametests.

**Tech Stack:** Java 25, Gradle 9.6.0, Fabric Loom 1.17, Minecraft 26.3 (Mojang mappings), Fabric Loader 0.19.5, Fabric API 0.161.0+26.3 (same version in dev and in Prism), JUnit 5 for `common/`, Fabric gametest for `fabric/`.

**Spec:** None exists in this repo. The authorities are `Roadmap.md` (feature priorities), `Content.md` (block hardness, light, tool data), and the original 1.7.10 source. Any ruling not covered by these is provisional and must be recorded in the progress ledger.

## Global Constraints

- Target Minecraft 26.3, Fabric Loader 0.19.5, Fabric API 0.161.0+26.3, Java 25. Do not change these without a ledger ruling.
- `common/` must not import `net.minecraft` or `net.fabricmc`.
- Identifier paths must match `[a-z0-9/._-]`. Every texture, model, blockstate, and registry path is lowercase. Rename files, do not map them in code.
- Blockstates and item models use vanilla Fabric JSON only. No Forge `forge_marker`, `forge:default-block`, or `forge:fluid` references.
- The fabric jar must bundle the `common/` output (`jar { from(project(':common').sourceSets.main.output) }`).
- Every `.java` file in `common/src/main` and `fabric/src/main` carries the Traincraft LGPL header. Do not invent `@author` lines.
- Work happens on a branch named `port/fabric-26.3` cut from `master`. Pushes go to the `Temp` remote branch only. Never push to `master`, never open a PR, never force-push, without an explicit instruction.
- Do not reuse code from the old phase 1 attempt in `/tmp/traincraft-backup-20261011-182740` as-is. It is reference only.

## Review Focus

1. **Missing textures or models at load time.** A block or item whose model or texture path does not exist logs an error and renders as missing. Pinned by: Task 3 asset check that every registered id has a blockstate, item model, and every referenced texture.
2. **Uppercase or non-Identifier-safe asset names.** The crash that stopped the first launch. Pinned by: Task 3 asset-name check over all of `assets/traincraft`, failing on any character outside `[a-z0-9/._-]`.
3. **Player places a facing block and it faces away.** Pinned by: Task 5 gametest that places each facing block with a given player yaw and asserts the stored FACING value.
4. **Furnace or distillery stays lit after fuel runs out.** Pinned by: Task 6 gametest that sets burn time to 0 and asserts the LIT state turns off.
5. **Steam locomotive loses fuel or water across save and reload.** Pinned by: Task 8 gametest that saves and reloads a world with fuel and water and asserts both values.

## Phases

Phases are ordered by dependency. Each phase ends with a jar that loads in Prism and a green `./gradlew :fabric:runGametest` and `./gradlew :common:test`.

### Task 1: Build foundation

**Files:**
- Create: `settings.gradle`, `build.gradle`, `gradle.properties`, `common/build.gradle`, `fabric/build.gradle`, `fabric/src/main/resources/fabric.mod.json`
- Create: `scripts/verify_headers.sh`, `scripts/verify_assets.sh`
- Test: `common/src/test/java/traincraft/common/BuildSmokeTest.java`, `fabric/src/gametest/java/traincraft/fabric/BootGameTest.java`

**Interfaces:**
- Consumes: nothing.
- Produces: `:common` and `:fabric` Gradle projects; `./gradlew :fabric:jar`, `:fabric:runGametest`, `:common:test`; `scripts/verify_headers.sh <root>` and `scripts/verify_assets.sh <root>` exit non-zero on failure.

Success checks: the three Gradle tasks succeed on an empty mod; the boot gametest passes; the jar contains `fabric.mod.json` and the `common` classes.

### Task 2: Registries and names

**Files:**
- Create: `common/src/main/java/traincraft/common/registry/RegistryNames.java` (lists of block, item, entity paths, copied from the 1.12 `TCBlocks`/`TCItems` registration)
- Create: `fabric/src/main/java/traincraft/fabric/registry/TCBlocks.java`, `TCItems.java`, `TCEntities.java`
- Test: `fabric/src/gametest/java/traincraft/fabric/RegistrationGameTest.java`

**Interfaces:**
- Consumes: `RegistryNames.BLOCKS`, `RegistryNames.ITEMS`, `RegistryNames.ENTITIES`.
- Produces: `TCBlocks.get(String path)`, `TCItems.get(String path)`, duplicate-id guard that throws on double registration.

Success checks: every name in `RegistryNames` is in `BuiltInRegistries`; the gametest passes; `verify_assets.sh` passes.

### Task 3: Asset pipeline

**Files:**
- Create: `fabric/src/main/resources/assets/traincraft/blockstates/*.json`, `models/block/*.json`, `models/item/*.json` (vanilla format only)
- Modify: rename any uppercase texture file to lowercase (`git mv`); update every reference
- Test: `scripts/verify_assets.sh`, `fabric/src/gametest/java/traincraft/fabric/AssetGameTest.java`

**Interfaces:**
- Consumes: the original assets under `src/main/resources/assets/traincraft`.
- Produces: one blockstate and one item model per registered id; `verify_assets.sh` maps every renamed file so byte-identity still holds.

Success checks: `verify_assets.sh` exits 0; the asset gametest confirms each block and item resolves its model and textures; Prism logs no `Failed to load blockstate` or `IdentifierException`.

Known inputs to convert: the Forge `cube`, `cube_all`, and `forge:default-block` blockstates; `.obj` block models (bridge pillar, generator, lantern, stopper, switch stand, water wheel, wind mill, rolling stock) need a vanilla JSON conversion or a custom model class.

### Task 4: Simple blocks and ores

**Files:**
- Create: `fabric/src/main/java/traincraft/fabric/block/` basic blocks; hardness/resistance/light taken from `Content.md` in `TCBlocks.props`
- Test: `fabric/src/gametest/java/traincraft/fabric/SimpleBlockGameTest.java`

**Interfaces:**
- Consumes: `TCBlocks.get`.
- Produces: copper ore, petrol ore, oil sand, ballast, lantern (light 14).

Success checks: each block's hardness and light level match `Content.md`; ore blocks drop the correct items under the required tool tag.

### Task 5: Facing blocks

**Files:**
- Create: `fabric/src/main/java/traincraft/fabric/block/FacingBlock.java` (extends `HorizontalDirectionalBlock`, FACING set from the placer's opposite horizontal direction)
- Modify: `TCBlocks.java` factory selects `FacingBlock` for the six facing ids
- Create: blockstates with four variants per block (north/south/west/east, Y 0/180/270/90)
- Test: `FacingGameTest.java`

**Interfaces:**
- Consumes: `TCBlocks.FACING`.
- Produces: FACING property on assembly tables I/II/III, distillery, train workbench, hearth furnace.

Success checks: gametest places each block with a player yaw and asserts the front faces the player; in Prism the front texture faces the player.

### Task 6: Active state (distillery and hearth furnace)

**Files:**
- Create: `fabric/src/main/java/traincraft/fabric/block/LitBlock.java` (BooleanProperty LIT)
- Modify: `common/src/main/java/traincraft/common/fuel/` burn logic reused by the block entity
- Create: active-variant blockstates (`active=true` swaps the front texture and sets light 0.8)
- Test: `ActiveStateGameTest.java`

**Interfaces:**
- Consumes: `FuelRules.burnTicks`, the block entity from Task 7.
- Produces: `LIT` state flipped by the block entity when burn time starts or stops.

Success checks: the furnace and distillery show the on textures while burning and the off textures when fuel runs out (Review Focus 4).

### Task 7: Block entities, fuel, and menus

**Files:**
- Create: `fabric/src/main/java/traincraft/fabric/blockentity/DistilleryBlockEntity.java`, `HearthFurnaceBlockEntity.java`, `AssemblyTableBlockEntity.java`, `TrainWorkbenchBlockEntity.java`
- Create: `fabric/src/main/java/traincraft/fabric/gui/*Menu.java` using `ExtendedMenuType` and `ExtendedMenuProvider`
- Create: `common/src/main/java/traincraft/common/fuel/FuelRules.java` (`canInsert`, `burnTicks`, injected vanilla lookup)
- Test: `BlockEntityGameTest.java`

**Interfaces:**
- Consumes: `FuelRules`, `ValueInput`/`ValueOutput` for save data.
- Produces: `Menu` types registered in `TCMenus`; each menu validates on the server before inserting.

Success checks: items insert and extract through each menu; server rejects non-fuel inserts; contents survive save and reload.

### Task 8: Steam small locomotive

**Files:**
- Create: `fabric/src/main/java/traincraft/fabric/entity/SteamSmallEntity.java`, `client/SteamSmallRenderer.java`, `client/SteamSmallModel.java`, `network/SteamSmallFuelPayload.java`, `gui/SteamSmallMenu.java`, `gui/SteamSmallScreen.java`
- Create: `common/src/main/java/traincraft/common/steam/SteamLocomotiveState.java` (temperature, fuel, water; `toMap`/`fromMap`)
- Test: `SteamSmallGameTest.java`, `SteamSmallGuiGameTest.java`, `SteamSaveGameTest.java`

**Interfaces:**
- Consumes: `FuelRules`, `SteamLocomotiveState`.
- Produces: `SteamSmallEntity.interact` (sneak mounts, plain click opens the menu); `SteamSmallMenu` with a fuel slot at (8,53).

Success checks: sneak-click mounts; plain click opens the GUI; fuel and water survive reload (Review Focus 5); the model's Y orientation matches the original (manual check).

### Task 9: Armor, canister, wrench, connector, guide

**Files:**
- Create: `fabric/src/main/java/traincraft/fabric/item/armor/*`, `ItemCanister`, `ItemWrench`, `ItemConnector`, `ItemSkinChanger`, guide item
- Test: `ItemGameTest.java`

**Interfaces:**
- Consumes: `TCItems.get`.
- Produces: armor sets with the original equipment slots and stats; canister accepts any fluid like a bucket.

Success checks: each armor piece equips in the right slot; canister fills and empties from a fluid source.

### Task 10: Wind mill, water wheel, generator, bridge pillar, stopper, switch stand

**Files:**
- Create: block and block-entity classes for each; wind mill uses a capability replacing the 1.12 custom capability
- Test: `MachineGameTest.java`

**Interfaces:**
- Consumes: `TCBlocks`, `FuelRules` (diesel).
- Produces: each block placeable with its original output.

Success checks: wind mill output scales with the wind value; generator burns diesel; stopper and switch stand change state on redstone or interact.

### Task 11: Sounds, advancements, worldgen

**Files:**
- Create: sound events for the 28 horn/idle/run sounds that the original ships (currently missing `.ogg` files cause warnings)
- Create: advancements from the 1.7.10 achievements list
- Create: ore worldgen features

**Interfaces:**
- Consumes: `TCBlocks`, `TCItems`.
- Produces: `SoundEvents` per sound; advancement JSON per achievement.

Success checks: no `cannot add it to event` warnings in the log; advancements load (count matches the list); ores generate in new chunks.

### Task 12: Tracks (scope check before starting)

Tracks are the largest unfinished area. The Roadmap marks TAPI-compatible tracks as a separate later item. This task is deferred: it needs a ruling on whether to port the 1.7.10 track models or use new vanilla-style models, before any code is written.

### Task 13: Rolling stock expansion (deferred)

The Roadmap lists more than 90 rolling stock items. Only the steam small locomotive is in scope for this plan. The rest is deferred to follow-up plans, one per locomotive family.

### Task 14: Verification and acceptance

**Files:**
- Create: `docs/acceptance.md` (VERIFIED / NOT VERIFIED / MANUAL per item)

**Interfaces:**
- Consumes: outputs of Tasks 1–11.
- Produces: an acceptance record with each Review Focus item marked.

Success checks: `verify_headers.sh`, `verify_assets.sh`, `:common:test`, and `:fabric:runGametest` all pass; manual items (in-game mount and GUI, save and reload, model orientation) are recorded as MANUAL until a person confirms them in Prism.

---

## Self-Review

1. **Spec coverage:** there is no spec file. Coverage is checked against `Roadmap.md` (Items, Blocks, Rolling stock, GUIs, train logic, achievements, worldgen). Tasks 4–11 cover the items and blocks marked in the Roadmap. Tracks (Task 12) and the remaining rolling stock (Task 13) are explicitly deferred, not dropped.
2. **Step scan:** each task has files, interfaces, and success checks. Code bodies are left to the implementer, per the skill.
3. **Type consistency:** `FuelRules`, `SteamLocomotiveState`, `TCBlocks.get`, `TCItems.get` are named the same in every task that uses them.
4. **Review Focus:** all five items map to a named test in Tasks 3, 5, 6, and 8.
5. **Proportion:** the plan is a set of decisions, not a transcript. Full code bodies are left to implementers.

## Open rulings (recorded before execution)

- No spec exists. This plan is the authority until one is written. Rulings made without a spec are provisional.
- Track models (Task 12) need a ruling before work starts.
- Missing `.ogg` sound files: the original sound list is the reference; any sound not present in the repo is recorded as MANUAL, not invented.

## Execution Handoff

Plan complete and saved to `docs/superpowers/plans/2026-10-11-traincraft-fabric-26.3-port.md`. Please review the plan. Does it capture what you want, and which execution approach should we use?

- **Subagent-driven:** a fresh subagent per task, reviewed before the next starts. Most thorough; the tasks depend on each other's interfaces, so this is the safer choice.
- **Native:** I implement every task in this session, then one review of the whole branch. Cheaper and faster, with no review until the end.

I recommend **subagent-driven**, because the tasks share interfaces across many files and a mistake in an early task (for example, the asset naming rule) would hit every later one.
