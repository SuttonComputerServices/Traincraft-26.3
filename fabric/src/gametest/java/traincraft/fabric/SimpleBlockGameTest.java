/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("SimpleBlockGameTest.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import traincraft.common.registry.RegistryNames;

public class SimpleBlockGameTest {
	@GameTest
	public void hardnessResistanceAndLightMatchContent(GameTestHelper helper) {
		check(helper, "copper_ore", 3.0f, 5.0f, 0);
		check(helper, "petrol_ore", 3.0f, 5.0f, 0);
		check(helper, "ore_oil_sand", 3.0f, 3.0f, 0);
		check(helper, "assembly_table_1", 3.5f, 3.5f, 0);
		check(helper, "train_workbench", 1.7f, 1.7f, 0);
		check(helper, "lantern", 1.7f, 1.7f, 14);
		helper.succeed();
	}

	@GameTest
	public void everyBlockHasSelfDropLootTable(GameTestHelper helper) {
		for (String id : RegistryNames.BLOCKS)
			helper.assertTrue(SimpleBlockGameTest.class.getClassLoader()
					.getResource("data/traincraft/loot_table/blocks/" + id + ".json") != null,
				Component.literal("missing loot table " + id));
		helper.succeed();
	}

	private static void check(GameTestHelper helper, String id, float hardness, float resistance, int light) {
		Block block = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("traincraft", id));
		var state = block.defaultBlockState();
		helper.assertTrue(state.getDestroySpeed(helper.getLevel(), BlockPos.ZERO) == hardness,
			Component.literal(id + " hardness"));
		helper.assertTrue(block.getExplosionResistance() == resistance, Component.literal(id + " resistance"));
		helper.assertTrue(state.getLightEmission() == light, Component.literal(id + " light"));
	}
}
