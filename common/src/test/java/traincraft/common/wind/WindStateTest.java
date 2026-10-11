/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("WindStateTest.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.common.wind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;

class WindStateTest {
	@Test
	void stepMovesByAtMostOne() {
		WindState w = new WindState();
		w.setStrength(12);
		Random r = new Random(1);
		for (int i = 0; i < 1000; i++) {
			int before = w.strength();
			w.step(r);
			assertTrue(Math.abs(w.strength() - before) <= 1);
		}
	}

	@Test
	void energyTracksStrengthAndNeverNegative() {
		assertEquals(7, WindState.energyFor(7));
		assertEquals(0, WindState.energyFor(-3));
	}
}
