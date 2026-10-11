/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("BlockEntityGameTest.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import traincraft.fabric.blockentity.ItemBlockEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import traincraft.fabric.blockentity.ItemBlockEntity;
import traincraft.fabric.gui.TCBlockMenu;
import traincraft.fabric.registry.TCBlocks;

public class BlockEntityGameTest {
	@GameTest
	public void fuelSlotsRejectNonFuelOnServer(GameTestHelper helper) {
		ItemBlockEntity be = place(helper, "distillery");
		TCBlockMenu menu = menu(helper, be, TCBlockMenu.DISTILLERY);
		helper.assertTrue(!menu.getSlot(1).mayPlace(new ItemStack(Items.STONE)), Component.literal("stone accepted as fuel"));
		helper.assertTrue(menu.getSlot(1).mayPlace(new ItemStack(Items.COAL)), Component.literal("coal rejected as fuel"));
		helper.succeed();
	}

	@GameTest
	public void hearthFurnaceFuelSlotRejectsNonFuel(GameTestHelper helper) {
		ItemBlockEntity be = place(helper, "hearth_furnace");
		TCBlockMenu menu = menu(helper, be, TCBlockMenu.HEARTH_FURNACE);
		helper.assertTrue(!menu.getSlot(0).mayPlace(new ItemStack(Items.STONE)), Component.literal("stone accepted in furnace"));
		helper.assertTrue(menu.getSlot(0).mayPlace(new ItemStack(Items.COAL)), Component.literal("coal rejected in furnace"));
		helper.succeed();
	}

	@GameTest
	public void menuSlotInsertAndExtractReachContainer(GameTestHelper helper) {
		ItemBlockEntity be = place(helper, "assembly_table_1");
		helper.assertTrue(be.inventory().getContainerSize() == 26, Component.literal("assembly table size"));
		TCBlockMenu menu = menu(helper, be, TCBlockMenu.ASSEMBLY_TABLE);
		menu.getSlot(0).set(new ItemStack(Items.IRON_INGOT));
		helper.assertTrue(be.inventory().getItem(0).is(Items.IRON_INGOT), Component.literal("insert did not reach container"));
		menu.getSlot(0).set(ItemStack.EMPTY);
		helper.assertTrue(be.inventory().getItem(0).isEmpty(), Component.literal("extract did not reach container"));
		helper.succeed();
	}

	@GameTest
	public void itemsSurviveSaveAndReload(GameTestHelper helper) {
		ItemBlockEntity be = place(helper, "distillery");
		be.inventory().setItem(0, new ItemStack(Items.IRON_INGOT));
		var registries = helper.getLevel().registryAccess();
		CompoundTag tag = be.saveWithFullMetadata(registries);
		BlockEntity loaded = BlockEntity.loadStatic(helper.absolutePos(BlockPos.ZERO),
			TCBlocks.get("distillery").defaultBlockState(), tag, registries);
		helper.assertTrue(loaded instanceof ItemBlockEntity lbe && lbe.inventory().getItem(0).is(Items.IRON_INGOT),
			Component.literal("items lost on save and reload"));
		helper.succeed();
	}

	@GameTest
	public void breakingAContainerDropsItsContents(GameTestHelper helper) {
		ItemBlockEntity be = place(helper, "distillery");
		BlockPos pos = helper.absolutePos(BlockPos.ZERO);
		be.inventory().setItem(0, new ItemStack(Items.IRON_INGOT, 3));
		var state = helper.getLevel().getBlockState(pos);
		var player = helper.makeMockServerPlayerInLevel();
		state.getBlock().playerDestroy(helper.getLevel(), player, pos, state, be, ItemStack.EMPTY);
		boolean dropped = !helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(2)).isEmpty();
		helper.assertTrue(dropped, Component.literal("container contents were lost on break"));
		helper.succeed();
	}

	@GameTest
	public void burningLavaLeavesTheBucket(GameTestHelper helper) {
		ItemBlockEntity be = place(helper, "hearth_furnace");
		BlockPos pos = helper.absolutePos(BlockPos.ZERO);
		be.inventory().setItem(0, new ItemStack(Items.LAVA_BUCKET));
		ItemBlockEntity.serverTick(helper.getLevel(), pos, helper.getLevel().getBlockState(pos), be);
		helper.assertTrue(be.inventory().getItem(0).is(Items.BUCKET), Component.literal("burned lava did not leave a bucket"));
		helper.succeed();
	}

	private static ItemBlockEntity place(GameTestHelper helper, String blockId) {
		BlockPos pos = helper.absolutePos(BlockPos.ZERO);
		helper.getLevel().setBlock(pos, TCBlocks.get(blockId).defaultBlockState(), 3);
		return (ItemBlockEntity) helper.getLevel().getBlockEntity(pos);
	}

	private static TCBlockMenu menu(GameTestHelper helper, ItemBlockEntity be, TCBlockMenu.Layout layout) {
		return new TCBlockMenu(0, helper.makeMockServerPlayerInLevel().getInventory(), be.getBlockPos(), layout, be.inventory());
	}
}
