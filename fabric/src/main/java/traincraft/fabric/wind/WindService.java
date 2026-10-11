/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("WindService.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.wind;

import java.util.Random;
import net.minecraft.server.MinecraftServer;
import traincraft.common.wind.WindState;

/**
 * Server-wide wind. shortcut: held in memory, so a restart resets it to 0; the 1.12 version saved it per dimension,
 * upgrade to SavedData when wind matters across restarts.
 */
public final class WindService {
	private static final int PERIOD = 128;
	private static final WindState WIND = new WindState();
	private static final Random RANDOM = new Random();

	private WindService() {}

	public static int strength() {
		return WIND.strength();
	}

	public static void setStrength(int strength) {
		WIND.setStrength(strength);
	}

	public static void tick(MinecraftServer server) {
		if (server.getTickCount() % PERIOD == 0) WIND.step(RANDOM);
	}
}
