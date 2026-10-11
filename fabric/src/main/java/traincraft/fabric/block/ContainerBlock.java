/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("ContainerBlock.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import traincraft.fabric.blockentity.ItemBlockEntity;
import traincraft.fabric.gui.TCBlockMenu;
import traincraft.fabric.registry.TCBlockEntities;

/** A facing block that holds items and opens its menu on right-click. */
public class ContainerBlock extends FacingBlock implements EntityBlock {
	private final TCBlockMenu.Layout layout;

	public ContainerBlock(Properties props, TCBlockMenu.Layout layout) {
		super(props);
		this.layout = layout;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ItemBlockEntity(TCBlockEntities.type(layout.name()), layout, pos, state);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ItemBlockEntity be && player instanceof ServerPlayer sp)
			sp.openMenu(be);
		return InteractionResult.SUCCESS;
	}
}
