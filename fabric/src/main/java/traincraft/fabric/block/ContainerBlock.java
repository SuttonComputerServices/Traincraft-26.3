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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import traincraft.fabric.blockentity.ItemBlockEntity;
import traincraft.fabric.gui.TCBlockMenu;
import traincraft.fabric.registry.TCBlockEntities;

/** A facing block that holds items and opens its menu on right-click. */
public class ContainerBlock extends FacingBlock implements EntityBlock {
	/** True while a fuel block burns. Every container has it so the state definition can be built before the layout is set. */
	public static final BooleanProperty LIT = BooleanProperty.create("lit");

	private final TCBlockMenu.Layout layout;

	public ContainerBlock(Properties props, TCBlockMenu.Layout layout) {
		super(props);
		this.layout = layout;
		registerDefaultState(defaultBlockState().setValue(LIT, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(LIT);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide() || layout.fuelIndex() < 0 || (Object) type != TCBlockEntities.type(layout.name())) return null;
		BlockEntityTicker ticker = (lvl, pos, st, be) -> ItemBlockEntity.serverTick(lvl, pos, st, (ItemBlockEntity) be);
		return ticker;
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
