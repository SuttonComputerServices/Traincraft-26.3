/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("TCEntities.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import traincraft.fabric.entity.SteamSmallEntity;

public final class TCEntities {
	private static EntityType<SteamSmallEntity> steamSmall;

	private TCEntities() {}

	public static EntityType<SteamSmallEntity> steamSmall() {
		if (steamSmall == null) throw new IllegalStateException("Entity not registered: traincraft:steam_small");
		return steamSmall;
	}

	public static void register() {
		Identifier id = Identifier.fromNamespaceAndPath("traincraft", "steam_small");
		ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
		steamSmall = Registry.register(BuiltInRegistries.ENTITY_TYPE, key,
			EntityType.Builder.of(SteamSmallEntity::new, MobCategory.MISC).sized(1.2f, 1.856f).clientTrackingRange(10).build(key));
	}
}
