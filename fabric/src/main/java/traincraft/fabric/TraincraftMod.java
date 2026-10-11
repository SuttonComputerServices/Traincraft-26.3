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

import net.fabricmc.api.ModInitializer;
import traincraft.fabric.registry.TCBlocks;
import traincraft.fabric.registry.TCItems;

public class TraincraftMod implements ModInitializer {
	@Override
	public void onInitialize() {
		TCBlocks.register();
		TCItems.register();
	}
}
