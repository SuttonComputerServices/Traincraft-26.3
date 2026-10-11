/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("TCItems.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.registry;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import traincraft.common.registry.RegistryNames;

public final class TCItems {
	private static final Map<String, Item> ITEMS = new LinkedHashMap<>();

	private TCItems() {}

	public static Item get(String path) {
		Item i = ITEMS.get(path);
		if (i == null) throw new IllegalStateException("Item not registered: traincraft:" + path);
		return i;
	}

	private static void add(String path, boolean block) {
		Identifier id = Identifier.fromNamespaceAndPath("traincraft", path);
		if (ITEMS.containsKey(path) || BuiltInRegistries.ITEM.containsKey(id))
			throw new IllegalStateException("Duplicate item registration: " + id);
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
		Item.Properties p = new Item.Properties().setId(key);
		Item item = block ? new BlockItem(TCBlocks.get(path), p.useBlockDescriptionPrefix()) : new Item(TCArmor.apply(path, p));
		ITEMS.put(path, Registry.register(BuiltInRegistries.ITEM, key, item));
	}

	/** Must run after TCBlocks.register(). */
	public static void register() {
		RegistryNames.BLOCKS.forEach(p -> add(p, true));
		RegistryNames.ITEMS.forEach(p -> add(p, false));
	}
}
