/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("WindMillBlock.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import traincraft.fabric.blockentity.WindMillBlockEntity;
import traincraft.fabric.registry.TCBlockEntities;

public class WindMillBlock extends FacingBlock implements EntityBlock {
	public WindMillBlock(Properties props) {
		super(props);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new WindMillBlockEntity(pos, state);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide() || (Object) type != TCBlockEntities.windMill()) return null;
		BlockEntityTicker ticker = (lvl, pos, st, be) -> WindMillBlockEntity.serverTick(pos, (WindMillBlockEntity) be);
		return ticker;
	}
}
