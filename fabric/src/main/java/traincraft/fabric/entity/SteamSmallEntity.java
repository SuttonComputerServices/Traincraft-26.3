/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("SteamSmallEntity.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.entity;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import traincraft.common.fuel.FuelRules;
import traincraft.common.steam.SteamLocomotiveState;
import traincraft.fabric.gui.SteamSmallMenu;

/** Small steam locomotive. Sneak-click mounts it; a plain click opens its fuel and water menu. No driving physics yet. */
public class SteamSmallEntity extends Entity {
	public static final int BURN_SLOT = 0;
	public static final int WATER_SLOT = 1;
	public static final int STORAGE_FIRST = 2;
	public static final int SIZE = 11;

	private static final String[] STATE_KEYS = {"burn_time", "max_burn_time", "water_mb", "temperature"};

	private final SimpleContainer inventory = new SimpleContainer(SIZE);
	private SteamLocomotiveState state = new SteamLocomotiveState();

	public SteamSmallEntity(EntityType<?> type, Level level) {
		super(type, level);
	}

	public Container inventory() {
		return inventory;
	}

	public SteamLocomotiveState state() {
		return state;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		// One rider, as the 1.12 seat list had one seat.
		return getPassengers().isEmpty();
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide()) return;
		refillBurnSlot();
		ItemStack burn = inventory.getItem(BURN_SLOT);
		int nextFuelTicks = burn.isEmpty() ? 0 : FuelRules.burnTicks(itemId(burn));
		if (state.tickBurn(nextFuelTicks)) inventory.removeItem(BURN_SLOT, 1);
		ItemStack water = inventory.getItem(WATER_SLOT);
		if (water.is(Items.WATER_BUCKET) && state.addBucket()) inventory.setItem(WATER_SLOT, new ItemStack(Items.BUCKET));
	}

	/** Moves one fuel item from the storage slots into the empty burn slot. */
	private void refillBurnSlot() {
		if (!inventory.getItem(BURN_SLOT).isEmpty()) return;
		for (int i = STORAGE_FIRST; i < SIZE; i++) {
			ItemStack stack = inventory.getItem(i);
			if (!stack.isEmpty() && FuelRules.canInsert(itemId(stack))) {
				inventory.setItem(BURN_SLOT, inventory.removeItem(i, 1));
				return;
			}
		}
	}

	public static String itemId(ItemStack stack) {
		return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
		if (player.isShiftKeyDown()) {
			// Vanilla refuses to mount while sneaking (canRide), so the sneak-click forces the mount.
			if (!level().isClientSide()) player.startRiding(this, true, true);
			return InteractionResult.SUCCESS;
		}
		if (!level().isClientSide() && player instanceof ServerPlayer sp) {
			sp.openMenu(new ExtendedMenuProvider<Integer>() {
				@Override
				public Integer getScreenOpeningData(ServerPlayer p) {
					return getId();
				}

				@Override
				public Component getDisplayName() {
					return Component.translatable("entity.traincraft.steam_small");
				}

				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
					return new SteamSmallMenu(id, inv, SteamSmallEntity.this);
				}
			});
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		ContainerHelper.loadAllItems(input, inventory.getItems());
		Map<String, Double> values = new HashMap<>();
		values.put("burn_time", input.getDoubleOr("burn_time", 0.0));
		values.put("max_burn_time", input.getDoubleOr("max_burn_time", 0.0));
		values.put("water_mb", input.getDoubleOr("water_mb", 0.0));
		values.put("temperature", input.getDoubleOr("temperature", SteamLocomotiveState.DEFAULT_TEMPERATURE));
		state = SteamLocomotiveState.fromMap(values);
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		ContainerHelper.saveAllItems(output, inventory.getItems());
		Map<String, Double> values = state.toMap();
		for (String key : STATE_KEYS) output.putDouble(key, values.get(key));
	}
}
