/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("TCSounds.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import traincraft.common.registry.RegistryNames;

public final class TCSounds {
	private TCSounds() {}

	public static void register() {
		for (String name : RegistryNames.SOUNDS) {
			Identifier id = Identifier.fromNamespaceAndPath("traincraft", name);
			Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
		}
	}
}
