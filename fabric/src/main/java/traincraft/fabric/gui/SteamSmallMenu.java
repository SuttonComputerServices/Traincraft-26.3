/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("SteamSmallMenu.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.gui;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import traincraft.common.fuel.FuelRules;
import traincraft.common.steam.SteamLocomotiveState;
import traincraft.fabric.entity.SteamSmallEntity;
import traincraft.fabric.registry.TCEntities;

/** Fuel, water, and coal storage for the small steam locomotive, laid out as in the 1.12 container. */
public class SteamSmallMenu extends AbstractContainerMenu {
	private static ExtendedMenuType<SteamSmallMenu, Integer> type;

	private final SteamSmallEntity entity;
	private final ContainerData data;

	public SteamSmallMenu(int id, Inventory inventory, SteamSmallEntity entity) {
		super(type, id);
		this.entity = entity;
		boolean clientSide = inventory.player.level().isClientSide();
		addSlot(new Slot(entity.inventory(), SteamSmallEntity.BURN_SLOT, 8, 53) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				if (stack.isEmpty()) return false;
				// The client accepts; the server rejects non-fuel.
				return clientSide || FuelRules.canInsert(SteamSmallEntity.itemId(stack));
			}
		});
		addSlot(new Slot(entity.inventory(), SteamSmallEntity.WATER_SLOT, 32, 53) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(Items.WATER_BUCKET);
			}
		});
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				addSlot(new Slot(entity.inventory(), SteamSmallEntity.STORAGE_FIRST + j + i * 3, 80 + j * 18, 18 + i * 18));
		addStandardInventorySlots(inventory, 8, 84);

		this.data = new ContainerData() {
			private final int[] synced = new int[3];

			@Override
			public int get(int index) {
				if (clientSide) return synced[index];
				SteamLocomotiveState s = entity.state();
				return switch (index) {
					case 0 -> s.burnTime();
					case 1 -> s.maxBurnTime();
					default -> s.waterMb();
				};
			}

			@Override
			public void set(int index, int value) {
				synced[index] = value;
			}

			@Override
			public int getCount() {
				return 3;
			}
		};
		addDataSlots(data);
	}

	public static ExtendedMenuType<SteamSmallMenu, Integer> type() {
		if (type == null) throw new IllegalStateException("Menu not registered: traincraft:steam_small");
		return type;
	}

	public static void register() {
		type = Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath("traincraft", "steam_small"),
			new ExtendedMenuType<SteamSmallMenu, Integer>((id, inv, entityId) -> {
				Entity e = inv.player.level().getEntity(entityId);
				// Not loaded yet on the client: an empty stand-in keeps the connection alive.
				SteamSmallEntity loco = e instanceof SteamSmallEntity s ? s
					: new SteamSmallEntity(TCEntities.steamSmall(), inv.player.level());
				return new SteamSmallMenu(id, inv, loco);
			}, ByteBufCodecs.VAR_INT));
	}

	/** Burn progress, 0-1, for the screen. */
	public float burnFraction() {
		int max = data.get(1);
		return max <= 0 ? 0f : (float) data.get(0) / max;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		// Shift-click is not ported.
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return !entity.isRemoved() && player.distanceToSqr(Vec3.atCenterOf(entity.blockPosition())) < 64.0;
	}
}
