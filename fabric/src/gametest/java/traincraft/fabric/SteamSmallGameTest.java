/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("SteamSmallGameTest.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import traincraft.fabric.entity.SteamSmallEntity;
import traincraft.fabric.gui.SteamSmallMenu;
import traincraft.fabric.registry.TCEntities;

public class SteamSmallGameTest {
	@GameTest
	public void sneakClickMountsAndPlainClickOpensMenu(GameTestHelper helper) {
		SteamSmallEntity loco = helper.spawn(TCEntities.steamSmall(), new BlockPos(1, 2, 1));
		ServerPlayer player = helper.makeMockServerPlayerInLevel();

		player.setShiftKeyDown(true);
		loco.interact(player, InteractionHand.MAIN_HAND, Vec3.ZERO);
		helper.assertTrue(player.getVehicle() == loco, Component.literal("sneak-click did not mount"));

		player.stopRiding();
		player.setShiftKeyDown(false);
		loco.interact(player, InteractionHand.MAIN_HAND, Vec3.ZERO);
		helper.assertTrue(player.containerMenu instanceof SteamSmallMenu, Component.literal("plain click did not open the menu"));
		helper.succeed();
	}

	@GameTest
	public void coalBurnsAndWaterBucketFills(GameTestHelper helper) {
		SteamSmallEntity loco = helper.spawn(TCEntities.steamSmall(), new BlockPos(1, 2, 1));
		loco.inventory().setItem(SteamSmallEntity.BURN_SLOT, new ItemStack(Items.COAL));
		loco.inventory().setItem(SteamSmallEntity.WATER_SLOT, new ItemStack(Items.WATER_BUCKET));
		loco.tick();
		helper.assertTrue(loco.state().isBurning(), Component.literal("coal did not start burning"));
		helper.assertTrue(loco.inventory().getItem(SteamSmallEntity.BURN_SLOT).isEmpty(), Component.literal("coal not consumed"));
		helper.assertTrue(loco.state().waterMb() == 1000, Component.literal("water bucket did not fill tank"));
		helper.assertTrue(loco.inventory().getItem(SteamSmallEntity.WATER_SLOT).is(Items.BUCKET), Component.literal("bucket not returned"));
		helper.succeed();
	}

	@GameTest
	public void fuelWaterAndStorageSurviveReload(GameTestHelper helper) {
		SteamSmallEntity loco = helper.spawn(TCEntities.steamSmall(), new BlockPos(1, 2, 1));
		loco.inventory().setItem(SteamSmallEntity.BURN_SLOT, new ItemStack(Items.COAL, 1));
		loco.inventory().setItem(SteamSmallEntity.STORAGE_FIRST, new ItemStack(Items.COAL, 5));
		loco.state().addBucket();
		loco.state().addBucket();

		var registries = helper.getLevel().registryAccess();
		TagValueOutput out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
		loco.saveWithoutId(out);
		CompoundTag tag = out.buildResult();

		SteamSmallEntity fresh = new SteamSmallEntity(TCEntities.steamSmall(), helper.getLevel());
		fresh.load(TagValueInput.create(ProblemReporter.DISCARDING, registries, tag));
		helper.assertTrue(fresh.inventory().getItem(SteamSmallEntity.BURN_SLOT).is(Items.COAL), Component.literal("burn slot lost"));
		helper.assertTrue(fresh.inventory().getItem(SteamSmallEntity.STORAGE_FIRST).getCount() == 5, Component.literal("storage lost"));
		helper.assertTrue(fresh.state().waterMb() == 2000, Component.literal("water lost"));
		helper.succeed();
	}
}
