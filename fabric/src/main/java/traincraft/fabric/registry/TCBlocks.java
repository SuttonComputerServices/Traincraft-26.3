/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("TCBlocks.java") is part of the Traincraft mod for Minecraft.
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import traincraft.common.registry.RegistryNames;

public final class TCBlocks {
	private static final Map<String, Block> BLOCKS = new LinkedHashMap<>();

	private TCBlocks() {}

	public static Block get(String path) {
		Block b = BLOCKS.get(path);
		if (b == null) throw new IllegalStateException("Block not registered: traincraft:" + path);
		return b;
	}

	/** Hardness, resistance, and light from Content.md. Harvest tools are in data tags. */
	private static Properties props(String path) {
		return switch (path) {
			case "assembly_table_1", "assembly_table_2", "assembly_table_3", "bridge_pillar", "distillery",
				"hearth_furnace" -> Properties.of().strength(3.5f);
			case "copper_ore", "petrol_ore" -> Properties.of().strength(3.0f, 5.0f);
			case "ore_oil_sand" -> Properties.of().strength(3.0f);
			case "lantern" -> Properties.of().strength(1.7f).lightLevel(s -> 14); // 0.98 * 15, rounded down
			default -> Properties.of().strength(1.7f);
		};
	}

	public static void register() {
		for (String path : RegistryNames.BLOCKS) {
			Identifier id = Identifier.fromNamespaceAndPath("traincraft", path);
			if (BLOCKS.containsKey(path) || BuiltInRegistries.BLOCK.containsKey(id))
				throw new IllegalStateException("Duplicate block registration: " + id);
			ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
			BLOCKS.put(path, Registry.register(BuiltInRegistries.BLOCK, key, new Block(props(path).setId(key))));
		}
	}
}
