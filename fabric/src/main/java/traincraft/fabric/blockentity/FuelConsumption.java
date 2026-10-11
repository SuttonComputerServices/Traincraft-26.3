/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("FuelConsumption.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.blockentity;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public final class FuelConsumption {
	private FuelConsumption() {}

	/** Removes one fuel item from the slot and puts its crafting remainder (lava bucket -> bucket) in its place. */
	public static void consumeOne(Container container, int index) {
		ItemStack fuel = container.getItem(index);
		ItemStackTemplate template = fuel.getItem().getCraftingRemainder(); // null for most items
		container.removeItem(index, 1);
		if (template != null && container.getItem(index).isEmpty()) container.setItem(index, template.create());
	}
}
