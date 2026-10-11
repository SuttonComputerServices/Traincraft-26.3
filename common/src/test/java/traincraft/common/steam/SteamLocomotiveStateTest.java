/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("SteamLocomotiveStateTest.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.common.steam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class SteamLocomotiveStateTest {
	@Test
	void fuelIsConsumedOnlyWhenIdleAndCountsDown() {
		SteamLocomotiveState s = new SteamLocomotiveState();
		assertFalse(s.tickBurn(0));
		assertTrue(s.tickBurn(3));
		assertTrue(s.isBurning());
		assertFalse(s.tickBurn(3));
		assertEquals(2, s.burnTime());
		assertFalse(s.tickBurn(3));
		assertEquals(1, s.burnTime());
		// Out of fuel: the last tick ends the burn, and a restart needs a new fuel item.
		assertFalse(s.tickBurn(0));
		assertFalse(s.isBurning());
		assertTrue(s.tickBurn(3));
	}

	@Test
	void waterStopsAtCapacity() {
		SteamLocomotiveState s = new SteamLocomotiveState();
		for (int i = 0; i < SteamLocomotiveState.WATER_CAPACITY_MB / SteamLocomotiveState.BUCKET_MB; i++) assertTrue(s.addBucket());
		assertFalse(s.addBucket());
		assertEquals(SteamLocomotiveState.WATER_CAPACITY_MB, s.waterMb());
	}

	@Test
	void mapRoundTripKeepsFuelWaterAndTemperature() {
		SteamLocomotiveState s = new SteamLocomotiveState();
		s.tickBurn(120);
		s.addBucket();
		SteamLocomotiveState back = SteamLocomotiveState.fromMap(s.toMap());
		assertEquals(120, back.burnTime());
		assertEquals(120, back.maxBurnTime());
		assertEquals(1000, back.waterMb());
		assertEquals(SteamLocomotiveState.DEFAULT_TEMPERATURE, back.temperature(), 1e-9);
	}

	@Test
	void fromMapClampsBadValues() {
		SteamLocomotiveState s = SteamLocomotiveState.fromMap(Map.of("water_mb", 99999.0, "temperature", 9999.0, "burn_time", -5.0));
		assertEquals(SteamLocomotiveState.WATER_CAPACITY_MB, s.waterMb());
		assertEquals(SteamLocomotiveState.MAX_TEMPERATURE, s.temperature(), 1e-9);
		assertEquals(0, s.burnTime());
	}
}
