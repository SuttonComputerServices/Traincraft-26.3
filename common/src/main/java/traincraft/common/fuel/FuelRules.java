/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("FuelRules.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.common.fuel;

import java.util.function.Function;

/** Fuel decisions shared by every fuel slot. The vanilla burn-time lookup is server-only, so the mod injects it. */
public final class FuelRules {
	private static Function<String, Integer> vanillaLookup = id -> null;

	private FuelRules() {}

	/** Injected by the loader adapter at startup; returns null when the item is not a fuel or no server is running. */
	public static void setVanillaLookup(Function<String, Integer> lookup) {
		vanillaLookup = lookup;
	}

	public static int burnTicks(String itemId) {
		if (itemId == null || itemId.isEmpty()) return 0;
		Integer ticks = vanillaLookup.apply(itemId);
		return ticks == null ? 0 : ticks;
	}

	public static boolean canInsert(String itemId) {
		return burnTicks(itemId) > 0;
	}
}
