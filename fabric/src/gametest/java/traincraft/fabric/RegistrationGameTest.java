/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("RegistrationGameTest.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import traincraft.common.registry.RegistryNames;

public class RegistrationGameTest {
	@GameTest
	public void allBlocksRegistered(GameTestHelper helper) {
		for (String p : RegistryNames.BLOCKS)
			helper.assertTrue(BuiltInRegistries.BLOCK.containsKey(Identifier.fromNamespaceAndPath("traincraft", p)),
				net.minecraft.network.chat.Component.literal("missing block " + p));
		helper.succeed();
	}

	@GameTest
	public void allItemsRegistered(GameTestHelper helper) {
		for (String p : RegistryNames.ITEMS)
			helper.assertTrue(BuiltInRegistries.ITEM.containsKey(Identifier.fromNamespaceAndPath("traincraft", p)),
				net.minecraft.network.chat.Component.literal("missing item " + p));
		helper.succeed();
	}
}
