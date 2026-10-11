/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("TCBlockMenu.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import traincraft.common.fuel.FuelRules;
import traincraft.fabric.blockentity.ItemBlockEntity;

/** One menu for every item container. Slot positions are the 1.12 container coordinates. */
public class TCBlockMenu extends AbstractContainerMenu {
	public record SlotSpec(int index, int x, int y, boolean fuel) {}

	public record Layout(String name, int size, int playerY, List<SlotSpec> slots) {
		/** Index of the fuel slot, or -1 when the block burns nothing. */
		public int fuelIndex() {
			return slots.stream().filter(SlotSpec::fuel).mapToInt(SlotSpec::index).findFirst().orElse(-1);
		}
	}

	public static final Layout DISTILLERY = new Layout("distillery", 5, 84, List.of(
		new SlotSpec(0, 56, 17, false), new SlotSpec(1, 56, 53, true), new SlotSpec(2, 116, 60, false),
		new SlotSpec(3, 123, 8, false), new SlotSpec(4, 123, 33, false)));

	public static final Layout HEARTH_FURNACE = new Layout("hearth_furnace", 1, 84, List.of(new SlotSpec(0, 56, 53, true)));

	public static final Layout ASSEMBLY_TABLE = new Layout("assembly_table", 26, 174, assemblySlots());

	public static final Layout TRAIN_WORKBENCH = new Layout("train_workbench", 9, 84, workbenchSlots());

	private static final Map<String, ExtendedMenuType<TCBlockMenu, BlockPos>> TYPES = new HashMap<>();

	private final BlockPos pos;
	private final Layout layout;

	public TCBlockMenu(int id, Inventory inventory, BlockPos pos, Layout layout, Container container) {
		super(TYPES.get(layout.name()), id);
		this.pos = pos;
		this.layout = layout;
		boolean clientSide = inventory.player.level().isClientSide();
		for (SlotSpec s : layout.slots()) {
			addSlot(new Slot(container, s.index(), s.x(), s.y()) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					if (!s.fuel()) return true;
					if (stack.isEmpty()) return false;
					// The client cannot see the server's burn-time lookup, so it accepts and the server rejects.
					return clientSide || FuelRules.canInsert(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
				}
			});
		}
		addStandardInventorySlots(inventory, 8, layout.playerY());
	}

	public static void register() {
		for (Layout layout : List.of(DISTILLERY, HEARTH_FURNACE, ASSEMBLY_TABLE, TRAIN_WORKBENCH)) {
			ExtendedMenuType<TCBlockMenu, BlockPos> type = new ExtendedMenuType<>((id, inv, pos) ->
				new TCBlockMenu(id, inv, pos, layout, containerFor(inv, pos, layout)), BlockPos.STREAM_CODEC);
			TYPES.put(layout.name(), Registry.register(BuiltInRegistries.MENU,
				Identifier.fromNamespaceAndPath("traincraft", layout.name()), type));
		}
	}

	public static MenuType<?> type(Layout layout) {
		return TYPES.get(layout.name());
	}

	private static Container containerFor(Inventory inv, BlockPos pos, Layout layout) {
		BlockEntity be = inv.player.level().getBlockEntity(pos);
		return be instanceof ItemBlockEntity ibe ? ibe.inventory() : new SimpleContainer(layout.size());
	}

	private static List<SlotSpec> assemblySlots() {
		// 1.12 ContainerAssemblyTable: 10 input slots, then a 4x4 grid (indices 10-25). Crafting results are not ported yet.
		List<SlotSpec> s = new java.util.ArrayList<>(List.of(
			new SlotSpec(0, 25, 27, false), new SlotSpec(1, 79, 27, false), new SlotSpec(2, 115, 27, false),
			new SlotSpec(3, 145, 27, false), new SlotSpec(4, 25, 61, false), new SlotSpec(5, 79, 61, false),
			new SlotSpec(6, 115, 61, false), new SlotSpec(7, 43, 93, false), new SlotSpec(8, 79, 93, false),
			new SlotSpec(9, 145, 93, false)));
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) s.add(new SlotSpec(10 + j + i * 4, 8 + j * 18, 128 + i * 18, false));
		return s;
	}

	private static List<SlotSpec> workbenchSlots() {
		// 1.12 ContainerTrainWorkbench 3x3 grid. The crafting result is not ported yet.
		List<SlotSpec> s = new java.util.ArrayList<>();
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) s.add(new SlotSpec(j + i * 3, 30 + j * 18, 17 + i * 18, false));
		return s;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		// Shift-click is not ported (the 1.12 distillery had the same gap).
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return player.level().getBlockEntity(pos) instanceof ItemBlockEntity && player.distanceToSqr(Vec3.atCenterOf(pos)) < 64.0;
	}
}
