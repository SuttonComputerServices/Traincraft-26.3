# Traincraft Fabric 26.3 port: acceptance record

Branch `port/fabric-26.3`, base `master` (0e61e5b4). Status key:
- **VERIFIED**: checked by an automated test or script in this repo, output read.
- **NOT VERIFIED**: no test covers it yet, or the check failed.
- **MANUAL**: needs a person to run the game (Prism) and look.

Automated checks (run at HEAD):
- `./gradlew :common:test`: 11 of 11 pass.
- `./gradlew :fabric:runGametest`: 23 of 23 pass.
- `./gradlew :fabric:jar`: builds.
- `scripts/verify_headers.sh .`: exit 0.
- `scripts/verify_assets.sh . .`: exit 0 (byte check for unchanged assets; converted files are covered by AssetGameTest).

## Review Focus (plan)

| # | Item | Status | Evidence / gap |
|---|------|--------|----------------|
| 1 | Missing textures or models at load time | VERIFIED | AssetGameTest: every registered block and item has definitions and models, and every referenced `traincraft:` texture exists. Loads from classpath (gametest server only loads data packs). |
| 2 | Uppercase or non-Identifier-safe names | VERIFIED | RegistryNamesTest: every registry path matches `[a-z0-9/._-]+`. Assets were lowercased on copy. |
| 3 | Facing block faces away from the player | VERIFIED | FacingGameTest: placed at yaw 0 and 180 across the 12 facing blocks. In-game front texture: MANUAL. |
| 4 | Furnace or distillery stays lit after fuel runs out | VERIFIED | LitGameTest drives serverTick for coal (1600 ticks). Live ticker path in game: MANUAL. |
| 5 | Steam locomotive loses fuel or water across save and reload | VERIFIED | SteamSmallGameTest.fuelWaterAndStorageSurviveReload; SteamLocomotiveStateTest map round-trip. |

## Tasks

| Task | Scope | Status | Evidence / gap |
|------|-------|--------|----------------|
| 1 | Gradle foundation | VERIFIED | Build and gametest run. Legacy Forge build replaced (ruling). |
| 2 | Registries and names | VERIFIED | RegistryNamesTest, RegistrationGameTest. Entity names belong to Task 8. |
| 3 | Asset pipeline | VERIFIED (files) / MANUAL (look) | verify_assets exit 0; AssetGameTest. Placeholder textures and cube shapes for 8 blocks and 3 items (rulings). Recipes not converted: **PLAN GAP**. |
| 4 | Simple blocks and ores | VERIFIED (stats) / MANUAL (drops) | SimpleBlockGameTest: hardness, resistance, light; loot-table presence. Ore drop roll in game: MANUAL. |
| 5 | Facing blocks | VERIFIED | FacingGameTest over 12 blocks (Content.md list, wider than the plan's 6). |
| 6 | Active state (distillery, furnace) | VERIFIED (logic) / MANUAL (ticker) | LitGameTest. Smelting and distilling progress not ported (ruling). |
| 7 | Block entities, fuel, menus | VERIFIED | BlockEntityGameTest (4): fuel rejects non-fuel on server, insert/extract, save and reload. Crafting results and recipes not ported: **PLAN GAP**. Menu layout in game: MANUAL. |
| 8 | Steam small locomotive | VERIFIED (logic) / MANUAL (feel) | SteamSmallGameTest (3): mount, menu open, burn and fill, reload. No physics, no item to obtain, no burn/water bars, default skin only. Seat position, model orientation: MANUAL. |
| 9 | Armor, canister, wrench, connector, guide | VERIFIED (armor) / NOT VERIFIED (canister) | ArmorGameTest (2): slots and equip, texture assets present. Canister fill and empty: **NOT VERIFIED, plan gap** (no fluid system). Composite suit effects not ported. |
| 10 | Machines | VERIFIED (wind mill, switch) / NOT VERIFIED (generator) | MachineGameTest (2); WindStateTest. Generator burning diesel: **NOT VERIFIED, plan gap**. Switch stand toggle is invented (no 1.12 source). |
| 11 | Sounds, advancements, worldgen | VERIFIED (registry, load) / NOT VERIFIED (in-game sound) | ContentGameTest (3): sounds registered, ore placed features load, 14 advancements load. 14 of 37 1.7.10 achievements ported: **PLAN GAP** against "count matches the list". |
| 14 | Acceptance and final review | this document + final review (see ledger) | |

## Known gaps (need a decision)

1. Recipes (crafting, smelting, distilling, assembly, workbench) not ported. No plan task covers them.
2. Fluids not ported: canister fill and empty, diesel generator, oil and diesel fluids.
3. No item to obtain the steam locomotive (only `/summon`).
4. Advancements: 14 of 37 achievements. The rest need vehicles or fluids.
5. Generator has no logic (1.12 had none).
6. Driving physics and rolling-stock API not ported (locomotive cannot move).
7. Placeholder art: 3 blocks and 3 items have no texture in the repo; 5 blocks are cubes where the original used OBJ shapes.

## Manual checks (Prism, Minecraft 26.3, Fabric Loader 0.19.5)

- Launch with no blockstate, model, or item errors in `logs/latest.log`.
- Facing blocks: front texture faces the placing player.
- Furnace and distillery: lit textures while burning, light level 12, off after fuel runs out.
- Assembly grid (y 128–182) does not look wrong over the player inventory row (y 174).
- Steam locomotive: sneak-click mounts, plain click opens the menu, model orientation, seat position.
- Armor: worn pieces show the right layers and colors (tint not ported).
- Ores generate in new chunks.
- Sounds play (once a block or entity calls them).
