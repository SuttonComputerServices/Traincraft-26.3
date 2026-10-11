/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("ItemBlockEntity.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.blockentity;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import traincraft.fabric.gui.TCBlockMenu;

/** Holds a block's items and opens its menu. */
public class ItemBlockEntity extends BlockEntity implements ExtendedMenuProvider<BlockPos> {
	private final TCBlockMenu.Layout layout;
	private final SimpleContainer inventory;

	public ItemBlockEntity(BlockEntityType<ItemBlockEntity> type, TCBlockMenu.Layout layout, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.layout = layout;
		this.inventory = new SimpleContainer(layout.size()) {
			@Override
			public void setChanged() {
				super.setChanged();
				ItemBlockEntity.this.setChanged();
			}
		};
	}

	public Container inventory() {
		return inventory;
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		ContainerHelper.saveAllItems(output, inventory.getItems());
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		ContainerHelper.loadAllItems(input, inventory.getItems());
	}

	@Override
	public BlockPos getScreenOpeningData(ServerPlayer player) {
		return getBlockPos();
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("block.traincraft." + layout.name());
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new TCBlockMenu(id, inv, getBlockPos(), layout, inventory);
	}
}
