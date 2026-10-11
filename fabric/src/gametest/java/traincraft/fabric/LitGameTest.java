/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("LitGameTest.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import traincraft.fabric.block.ContainerBlock;
import traincraft.fabric.blockentity.ItemBlockEntity;
import traincraft.fabric.registry.TCBlocks;

public class LitGameTest {
	@GameTest
	public void hearthFurnaceLitWhileBurningAndOffWhenFuelRunsOut(GameTestHelper helper) {
		burnCycle(helper, "hearth_furnace", 0);
		helper.succeed();
	}

	@GameTest
	public void distilleryLitWhileBurningAndOffWhenFuelRunsOut(GameTestHelper helper) {
		burnCycle(helper, "distillery", 1);
		helper.succeed();
	}

	// Coal burns 1600 ticks (vanilla cooking_fuel). Lit on the first tick, off once the last tick has counted down.
	private static void burnCycle(GameTestHelper helper, String blockId, int fuelSlot) {
		BlockPos pos = helper.absolutePos(BlockPos.ZERO);
		Level level = helper.getLevel();
		level.setBlock(pos, TCBlocks.get(blockId).defaultBlockState(), 3);
		ItemBlockEntity be = (ItemBlockEntity) level.getBlockEntity(pos);
		be.inventory().setItem(fuelSlot, new ItemStack(Items.COAL));

		tick(level, pos, be);
		helper.assertTrue(lit(level, pos), Component.literal(blockId + " not lit while burning"));
		helper.assertTrue(be.inventory().getItem(fuelSlot).isEmpty(), Component.literal(blockId + " did not consume fuel"));

		for (int i = 0; i < 1600; i++) tick(level, pos, be);
		helper.assertTrue(!lit(level, pos), Component.literal(blockId + " still lit after fuel ran out"));
	}

	private static void tick(Level level, BlockPos pos, ItemBlockEntity be) {
		ItemBlockEntity.serverTick(level, pos, level.getBlockState(pos), be);
	}

	private static boolean lit(Level level, BlockPos pos) {
		BlockState s = level.getBlockState(pos);
		return s.getValue(ContainerBlock.LIT);
	}
}
