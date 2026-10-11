/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("RegistryNames.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.common.registry;

import java.util.List;

/** Registry paths (namespace "traincraft"). Block and item lists follow the 1.12 TCBlocks and TCItems fields. */
public final class RegistryNames {
	private RegistryNames() {}

	public static final List<String> BLOCKS = List.of(
		"distillery", "assembly_table_1", "assembly_table_2", "assembly_table_3", "train_workbench",
		"hearth_furnace", "water_wheel", "wind_mill", "generator_diesel", "battery", "stopper",
		"bridge_pillar", "lantern", "ballast", "ore_oil_sand", "petrol_ore", "copper_ore",
		// Not in the 1.12 registry; from Roadmap.md and Task 10 of the plan.
		"switch_stand"
	);

	/** Block items are registered from BLOCKS; these are the non-block items. */
	public static final List<String> ITEMS = List.of(
		"wrench", "skin_changer", "canister", "connector", "guide", "steel_ingot", "steel_dust",
		"coal_dust", "graphite", "steel_firebox", "steel_bogie", "steel_frame", "steel_cabin",
		"steel_chimney", "plastic", "copper_ingot", "balloon", "bogie_iron", "bogie_wood",
		"boiler_iron", "boiler_steel", "cab_iron", "cab_wood", "camshaft", "chimney_iron", "circuit",
		"controls", "cylinder", "engine_diesel", "engine_electric", "engine_steam", "fiberglass_plate",
		"firebox_iron", "frame_iron", "frame_wood", "piston", "propeller", "rail_copper", "rail_steel",
		"reinforced_plate", "seats", "signal", "transformer", "transmission", "fine_copper_wire",
		"chunk_loader_activator", "armor_overalls", "armor_jacket", "armor_hat", "armor_ticketman_jacket",
		"armor_ticketman_pants", "armor_ticketman_hat", "armor_driver_jacket", "armor_driver_pants",
		"armor_driver_hat", "armor_composite_suit_head", "armor_composite_suit_chest",
		"armor_composite_suit_legs", "armor_composite_suit_feet"
	);

	public static final List<String> ENTITIES = List.of("locomotive_steam_small");

	/** Sound events; every name must be a key in assets/traincraft/sounds.json. */
	public static final List<String> SOUNDS = List.of(
		"742_horn",
		"742_motor",
		"742_motor_fast",
		"742_motor_slow",
		"adler_run",
		"adler_whistle",
		"american_steam_horn",
		"british_two_tone",
		"chme3_horn",
		"chme3_idle",
		"chme3_run",
		"class62_horn",
		"eu07_horn",
		"german_steam_horn",
		"gp40-2_horn",
		"gp_horn",
		"high_speed_horn",
		"mg_horn",
		"mg_idle",
		"mg_run",
		"mtc_overspeed",
		"mtc_speedchange",
		"sd70_horn",
		"shay_horn",
		"shunter_horn",
		"steam_horn",
		"steam_idle",
		"steam_run",
		"subway_horn",
		"tram_horn",
		"v60_horn",
		"vl10_horn",
		"vl10_idle",
		"vl10_run"
	);
}
