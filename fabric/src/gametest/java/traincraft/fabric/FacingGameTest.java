/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("FacingGameTest.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric;

import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import traincraft.fabric.registry.TCBlocks;

public class FacingGameTest {
	@GameTest
	public void frontFacesThePlacingPlayer(GameTestHelper helper) {
		for (String id : TCBlocks.FACING) {
			Block block = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("traincraft", id));
			// Minecraft yaw 0 looks south, so the front faces north; yaw 180 looks north, so it faces south.
			helper.assertTrue(placedFacing(helper, block, 0f) == Direction.NORTH, Component.literal(id + " yaw 0"));
			helper.assertTrue(placedFacing(helper, block, 180f) == Direction.SOUTH, Component.literal(id + " yaw 180"));
			helper.assertTrue(hasFacingVariants(id), Component.literal(id + " blockstate lacks facing=north/south/west/east"));
		}
		helper.succeed();
	}

	// The gametest server does not load block models, so the blockstate keys are checked from the classpath.
	private static boolean hasFacingVariants(String id) {
		try (var in = FacingGameTest.class.getClassLoader().getResourceAsStream("assets/traincraft/blockstates/" + id + ".json")) {
			if (in == null) return false;
			var variants = JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject().getAsJsonObject("variants");
			// Containers add lit=true/false to each facing, so 4 or 8 variants are valid.
			for (String d : new String[] {"north", "south", "west", "east"}) {
				boolean found = false;
				for (String key : variants.keySet()) found |= key.startsWith("facing=" + d);
				if (!found) return false;
			}
			return variants.size() == 4 || variants.size() == 8;
		} catch (IOException e) {
			return false;
		}
	}

	private static Direction placedFacing(GameTestHelper helper, Block block, float yaw) {
		var player = helper.makeMockServerPlayerInLevel();
		player.setYRot(yaw);
		BlockPos pos = helper.absolutePos(BlockPos.ZERO);
		var hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
		BlockPlaceContext ctx = new BlockPlaceContext(player, InteractionHand.MAIN_HAND, new ItemStack(block), hit);
		BlockState state = block.getStateForPlacement(ctx);
		return state.getValue(HorizontalDirectionalBlock.FACING);
	}
}
