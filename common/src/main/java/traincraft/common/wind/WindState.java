/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("WindState.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.common.wind;

import java.util.Random;

/** World wind strength, stepped by the 1.7 Traincraft algorithm. */
public final class WindState {
	/** Energy per wind point for each wind-mill tick batch (1.12 ENERGY_PER_WIND_STRENGTH). */
	public static final int ENERGY_PER_WIND_STRENGTH = 1;

	private int strength;

	public int strength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	/** One wind step: strength drifts up or down by at most one, biased back toward 10-20. */
	public void step(Random random) {
		int upChance = 10;
		int downChance = 10;
		if (strength > 20) {
			upChance -= strength - 20;
		} else if (strength < 10) {
			downChance -= 10 - strength;
		}
		if (random.nextInt(100) <= upChance) strength += 1;
		if (random.nextInt(100) <= downChance) strength -= 1;
	}

	/** Energy a wind mill makes in one batch at the given strength. */
	public static int energyFor(int strength) {
		return Math.max(0, strength * ENERGY_PER_WIND_STRENGTH);
	}
}
