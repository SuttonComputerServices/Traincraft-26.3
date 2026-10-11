/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("WindMillBlockEntity.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import traincraft.common.wind.WindState;
import traincraft.fabric.registry.TCBlockEntities;
import traincraft.fabric.wind.WindService;

/** Stores the energy a wind mill makes. Adds one batch every 10 ticks, scaled by the wind strength. */
public class WindMillBlockEntity extends BlockEntity {
	public static final int CAPACITY = 250;
	private static final int PERIOD = 10;

	private int energy;
	private int ticks;

	public WindMillBlockEntity(BlockPos pos, BlockState state) {
		super(TCBlockEntities.windMill(), pos, state);
	}

	public int energy() {
		return energy;
	}

	public static void serverTick(BlockPos pos, WindMillBlockEntity be) {
		if (++be.ticks % PERIOD != 0) return;
		int made = WindState.energyFor(WindService.strength());
		if (made > 0) {
			be.energy = Math.min(CAPACITY, be.energy + made);
			be.setChanged();
		}
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		output.putInt("energy", energy);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		energy = input.getIntOr("energy", 0);
	}
}
