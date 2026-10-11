/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("SwitchStandBlock.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.block;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Switch stand. The 1.12 source has no switch stand, so the toggle is a plan requirement (Task 10), not a ported behavior.
 * Redstone input is not implemented.
 */
public class SwitchStandBlock extends FacingBlock {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public SwitchStandBlock(Properties props) {
		super(props);
		registerDefaultState(defaultBlockState().setValue(ACTIVE, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ACTIVE);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide()) level.setBlock(pos, state.cycle(ACTIVE), 3);
		return InteractionResult.SUCCESS;
	}
}
