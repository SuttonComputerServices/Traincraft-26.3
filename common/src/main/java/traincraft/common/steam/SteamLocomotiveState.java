/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("SteamLocomotiveState.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.common.steam;

import java.util.Map;

/** Fuel, water, and temperature of a steam locomotive. Units: ticks, millibuckets, kelvin. */
public final class SteamLocomotiveState {
	public static final int WATER_CAPACITY_MB = 5000;
	public static final int BUCKET_MB = 1000;
	public static final double DEFAULT_TEMPERATURE = 293.15;
	public static final double MAX_TEMPERATURE = 473.15;
	public static final double MIN_TEMPERATURE = 253.15;

	private int burnTime;
	private int maxBurnTime;
	private int waterMb;
	private double temperature = DEFAULT_TEMPERATURE;

	public int burnTime() { return burnTime; }

	public int maxBurnTime() { return maxBurnTime; }

	public int waterMb() { return waterMb; }

	public double temperature() { return temperature; }

	public boolean isBurning() { return burnTime > 0; }

	/**
	 * Counts down the current fuel. Returns true when the caller should consume one fuel item
	 * (nothing is burning and nextFuelTicks is positive); that fuel then starts burning.
	 */
	public boolean tickBurn(int nextFuelTicks) {
		if (burnTime > 0) burnTime--;
		if (burnTime > 0 || nextFuelTicks <= 0) return false;
		burnTime = maxBurnTime = nextFuelTicks;
		return true;
	}

	/** Adds one bucket of water if the tank has room. */
	public boolean addBucket() {
		if (waterMb + BUCKET_MB > WATER_CAPACITY_MB) return false;
		waterMb += BUCKET_MB;
		return true;
	}

	public Map<String, Double> toMap() {
		return Map.of(
			"burn_time", (double) burnTime,
			"max_burn_time", (double) maxBurnTime,
			"water_mb", (double) waterMb,
			"temperature", temperature);
	}

	/** Missing keys keep their defaults; values are clamped to the valid ranges. */
	public static SteamLocomotiveState fromMap(Map<String, Double> map) {
		SteamLocomotiveState s = new SteamLocomotiveState();
		s.burnTime = Math.max(0, map.getOrDefault("burn_time", 0.0).intValue());
		s.maxBurnTime = Math.max(0, map.getOrDefault("max_burn_time", 0.0).intValue());
		s.waterMb = Math.min(WATER_CAPACITY_MB, Math.max(0, map.getOrDefault("water_mb", 0.0).intValue()));
		s.temperature = Math.min(MAX_TEMPERATURE, Math.max(MIN_TEMPERATURE, map.getOrDefault("temperature", DEFAULT_TEMPERATURE)));
		return s;
	}
}
