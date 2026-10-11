/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("ArmorGameTest.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class ArmorGameTest {
	private static final String[][] SLOTS = {
		{"armor_hat", "HEAD"}, {"armor_jacket", "CHEST"}, {"armor_overalls", "CHEST"},
		{"armor_ticketman_hat", "HEAD"}, {"armor_ticketman_jacket", "CHEST"}, {"armor_ticketman_pants", "LEGS"},
		{"armor_driver_hat", "HEAD"}, {"armor_driver_jacket", "CHEST"}, {"armor_driver_pants", "LEGS"},
		{"armor_composite_suit_head", "HEAD"}, {"armor_composite_suit_chest", "CHEST"},
		{"armor_composite_suit_legs", "LEGS"}, {"armor_composite_suit_feet", "FEET"},
	};

	@GameTest
	public void everyArmorPieceEquipsInItsSlot(GameTestHelper helper) {
		var player = helper.makeMockServerPlayerInLevel();
		for (String[] row : SLOTS) {
			Item item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("traincraft", row[0]));
			EquipmentSlot expected = EquipmentSlot.valueOf(row[1]);
			Equippable equippable = item.getDefaultInstance().get(DataComponents.EQUIPPABLE);
			helper.assertTrue(equippable != null && equippable.slot() == expected,
				Component.literal(row[0] + " is not equippable in " + expected));
			ItemStack stack = new ItemStack(item);
			player.setItemSlot(expected, stack);
			helper.assertTrue(player.getItemBySlot(expected) == stack, Component.literal(row[0] + " did not equip"));
			player.setItemSlot(expected, ItemStack.EMPTY);
		}
		helper.succeed();
	}

	@GameTest
	public void armorEquipmentAssetsAndTexturesExist(GameTestHelper helper) {
		// Each equipment asset names its humanoid texture, which must exist under textures/entity/equipment/humanoid.
		String[][] assets = {
			{"hat", "blue_hat"}, {"jacket", "orange_jacket"}, {"overalls", "blue_overalls"},
			{"ticketman", "ticketman"}, {"driver", "driver"}, {"composite_suit", "composite_suit"},
		};
		for (String[] a : assets) {
			helper.assertTrue(ArmorGameTest.class.getClassLoader().getResource("assets/traincraft/equipment/" + a[0] + ".json") != null,
				Component.literal("missing equipment asset " + a[0]));
			helper.assertTrue(ArmorGameTest.class.getClassLoader().getResource("assets/traincraft/textures/entity/equipment/humanoid/" + a[1] + ".png") != null,
				Component.literal("missing humanoid texture " + a[1]));
		}
		for (String legs : new String[] {"ticketman_pants", "driver_pants", "composite_suit_pants"})
			helper.assertTrue(ArmorGameTest.class.getClassLoader().getResource("assets/traincraft/textures/entity/equipment/humanoid_leggings/" + legs + ".png") != null,
				Component.literal("missing leggings texture " + legs));
		helper.succeed();
	}
}
