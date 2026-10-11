/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("MachineGameTest.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import traincraft.fabric.block.SwitchStandBlock;
import traincraft.fabric.blockentity.WindMillBlockEntity;
import traincraft.fabric.registry.TCBlocks;
import traincraft.fabric.wind.WindService;

public class MachineGameTest {
	@GameTest
	public void windMillOutputScalesWithWind(GameTestHelper helper) {
		BlockPos pos = helper.absolutePos(BlockPos.ZERO);
		helper.getLevel().setBlock(pos, TCBlocks.get("wind_mill").defaultBlockState(), 3);
		WindMillBlockEntity be = (WindMillBlockEntity) helper.getLevel().getBlockEntity(pos);

		WindService.setStrength(5);
		for (int i = 0; i < 10; i++) WindMillBlockEntity.serverTick(pos, be);
		helper.assertTrue(be.energy() == 5, Component.literal("wind 5 made " + be.energy() + ", expected 5"));

		be.setRemoved();
		WindService.setStrength(0);
		helper.succeed();
	}

	@GameTest
	public void switchStandTogglesOnUse(GameTestHelper helper) {
		BlockPos pos = helper.absolutePos(BlockPos.ZERO);
		SwitchStandBlock block = (SwitchStandBlock) TCBlocks.get("switch_stand");
		helper.getLevel().setBlock(pos, block.defaultBlockState(), 3);
		var hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);

		BlockState s = helper.getLevel().getBlockState(pos);
		helper.assertTrue(!s.getValue(SwitchStandBlock.ACTIVE), Component.literal("switch starts active"));
		block.useWithoutItem(s, helper.getLevel(), pos, helper.makeMockServerPlayerInLevel(), hit);
		helper.assertTrue(helper.getLevel().getBlockState(pos).getValue(SwitchStandBlock.ACTIVE), Component.literal("switch did not toggle on"));
		block.useWithoutItem(helper.getLevel().getBlockState(pos), helper.getLevel(), pos, helper.makeMockServerPlayerInLevel(), hit);
		helper.assertTrue(!helper.getLevel().getBlockState(pos).getValue(SwitchStandBlock.ACTIVE), Component.literal("switch did not toggle off"));
		helper.succeed();
	}
}
