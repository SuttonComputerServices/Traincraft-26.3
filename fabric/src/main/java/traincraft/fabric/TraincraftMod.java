/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("TraincraftMod.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric;

import java.util.Optional;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import traincraft.common.fuel.FuelRules;
import traincraft.fabric.gui.SteamSmallMenu;
import traincraft.fabric.gui.TCBlockMenu;
import traincraft.fabric.registry.TCEntities;
import traincraft.fabric.registry.TCSounds;
import traincraft.fabric.registry.TCBlockEntities;
import traincraft.fabric.wind.WindService;
import traincraft.fabric.registry.TCBlocks;
import traincraft.fabric.registry.TCItems;

public class TraincraftMod implements ModInitializer {
	private static volatile MinecraftServer server;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register(s -> server = s);
		ServerTickEvents.END_SERVER_TICK.register(WindService::tick);
		ServerLifecycleEvents.SERVER_STOPPED.register(s -> server = null);
		TCBlocks.register();
		TCItems.register();
		TCBlockEntities.register();
		TCBlockMenu.register();
		TCEntities.register();
		TCSounds.register();
		SteamSmallMenu.register();
		FuelRules.setVanillaLookup(TraincraftMod::vanillaBurnTicks);
	}

	/** Vanilla furnace burn time from the item's cooking_fuel component; null when not a fuel or no server. */
	private static Integer vanillaBurnTicks(String itemId) {
		MinecraftServer s = server;
		Identifier id = Identifier.tryParse(itemId);
		if (s == null || id == null) return null;
		ItemStack stack = BuiltInRegistries.ITEM.getValue(id).getDefaultInstance();
		CookingFuel fuel = stack.get(DataComponents.COOKING_FUEL);
		if (fuel == null) return null;
		LootContext ctx = new LootContext.Builder(new LootParams.Builder(s.overworld()).create(LootContextParamSets.EMPTY))
			.create(Optional.empty());
		return fuel.burnTime().get(ctx, 0);
	}
}
