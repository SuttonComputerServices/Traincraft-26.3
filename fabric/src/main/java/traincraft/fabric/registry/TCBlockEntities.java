/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("TCBlockEntities.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.registry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import traincraft.fabric.blockentity.ItemBlockEntity;
import traincraft.fabric.blockentity.WindMillBlockEntity;
import traincraft.fabric.gui.TCBlockMenu;
import traincraft.fabric.gui.TCBlockMenu.Layout;

public final class TCBlockEntities {
	private static final Map<String, BlockEntityType<ItemBlockEntity>> TYPES = new HashMap<>();

	private static BlockEntityType<WindMillBlockEntity> windMill;

	private TCBlockEntities() {}

	public static BlockEntityType<WindMillBlockEntity> windMill() {
		if (windMill == null) throw new IllegalStateException("Block entity not registered: wind_mill");
		return windMill;
	}

	public static BlockEntityType<ItemBlockEntity> type(String name) {
		BlockEntityType<ItemBlockEntity> t = TYPES.get(name);
		if (t == null) throw new IllegalStateException("Block entity not registered: " + name);
		return t;
	}

	/** Must run after TCBlocks.register(). */
	public static void register() {
		register(TCBlockMenu.DISTILLERY, "distillery");
		register(TCBlockMenu.HEARTH_FURNACE, "hearth_furnace");
		register(TCBlockMenu.ASSEMBLY_TABLE, "assembly_table_1", "assembly_table_2", "assembly_table_3");
		register(TCBlockMenu.TRAIN_WORKBENCH, "train_workbench");
		windMill = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath("traincraft", "wind_mill"),
			new BlockEntityType<WindMillBlockEntity>(WindMillBlockEntity::new, Set.of(TCBlocks.get("wind_mill"))));
	}

	private static void register(Layout layout, String... blockPaths) {
		Set<Block> blocks = Arrays.stream(blockPaths).map(TCBlocks::get).collect(Collectors.toSet());
		BlockEntityType<ItemBlockEntity> type = new BlockEntityType<ItemBlockEntity>(
			(pos, state) -> new ItemBlockEntity(TYPES.get(layout.name()), layout, pos, state), blocks);
		TYPES.put(layout.name(), Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
			Identifier.fromNamespaceAndPath("traincraft", layout.name()), type));
	}
}
