/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("FuelRulesTest.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.common.fuel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FuelRulesTest {
	@Test
	void nullAndEmptyAreNotFuel() {
		FuelRules.setVanillaLookup(id -> 200);
		assertFalse(FuelRules.canInsert(null));
		assertFalse(FuelRules.canInsert(""));
		assertEquals(0, FuelRules.burnTicks(null));
	}

	@Test
	void lookupDecidesFuelAndZeroIsNotFuel() {
		FuelRules.setVanillaLookup(id -> {
			if (id.equals("minecraft:coal")) return 1600;
			if (id.equals("minecraft:stone")) return 0;
			return null;
		});
		assertTrue(FuelRules.canInsert("minecraft:coal"));
		assertEquals(1600, FuelRules.burnTicks("minecraft:coal"));
		assertFalse(FuelRules.canInsert("minecraft:stone"));
		assertFalse(FuelRules.canInsert("minecraft:unknown"));
	}
}
