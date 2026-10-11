/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("ContentGameTest.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import traincraft.common.registry.RegistryNames;

public class ContentGameTest {
	@GameTest
	public void everySoundIsRegistered(GameTestHelper helper) {
		for (String name : RegistryNames.SOUNDS)
			helper.assertTrue(BuiltInRegistries.SOUND_EVENT.containsKey(Identifier.fromNamespaceAndPath("traincraft", name)),
				Component.literal("missing sound " + name));
		helper.succeed();
	}

	@GameTest
	// A placed feature only loads when the configured feature it names also loads.
	public void oreFeaturesAndPlacementsLoad(GameTestHelper helper) {
		var lookup = helper.getLevel().registryAccess();
		for (String ore : new String[] {"copper_ore", "petrol_ore", "ore_oil_sand"}) {
			Identifier id = Identifier.fromNamespaceAndPath("traincraft", ore);
			helper.assertTrue(lookup.lookupOrThrow(Registries.PLACED_FEATURE).get(ResourceKey.create(Registries.PLACED_FEATURE, id)).isPresent(),
				Component.literal("missing placed feature " + ore));
		}
		helper.succeed();
	}

	@GameTest
	public void achievementAdvancementsLoad(GameTestHelper helper) {
		var advancements = helper.getLevel().getServer().getAdvancements();
		for (String key : new String[] {"steel", "plastic", "fineCopperWire", "electronicCircuit", "generator", "firebox",
				"distilationTower", "openHearth", "trainWB", "assemblyTable", "engineer", "woodenParts", "ironParts", "steelParts"}) {
			Identifier id = Identifier.fromNamespaceAndPath("traincraft", "achievement_" + key.toLowerCase());
			helper.assertTrue(advancements.get(id) != null, Component.literal("missing advancement " + id));
		}
		helper.succeed();
	}
}
