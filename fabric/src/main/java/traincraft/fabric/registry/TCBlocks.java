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
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import traincraft.common.registry.RegistryNames;
import traincraft.fabric.block.ContainerBlock;
import traincraft.fabric.block.FacingBlock;
import traincraft.fabric.block.SwitchStandBlock;
import traincraft.fabric.block.WindMillBlock;
import traincraft.fabric.gui.TCBlockMenu;

public final class TCBlocks {
	private static final Map<String, Block> BLOCKS = new LinkedHashMap<>();

	private TCBlocks() {}

	public static Block get(String path) {
		Block b = BLOCKS.get(path);
		if (b == null) throw new IllegalStateException("Block not registered: traincraft:" + path);
		return b;
	}

	/** Hardness, resistance, and light from Content.md. Harvest tools are in data tags. */
	/** Blocks whose front faces the player on placement (Content.md north/east/south/west states). */
	public static final Set<String> FACING = Set.of(
		"assembly_table_1", "assembly_table_2", "assembly_table_3", "bridge_pillar", "generator_diesel", "distillery", "hearth_furnace", "stopper", "switch_stand", "train_workbench", "water_wheel", "wind_mill");

	/** Lit furnaces and distilleries glow at 12 (Content.md 0 or 12; 1.12 distillery 0.8 × 15). */
	private static int litLight(BlockState s) {
		return s.getValue(ContainerBlock.LIT) ? 12 : 0;
	}

	private static Properties props(String path) {
		return switch (path) {
			case "assembly_table_1", "assembly_table_2", "assembly_table_3", "bridge_pillar" -> Properties.of().strength(3.5f);
			case "distillery", "hearth_furnace" -> Properties.of().strength(3.5f).lightLevel(TCBlocks::litLight);
			case "copper_ore", "petrol_ore" -> Properties.of().strength(3.0f, 5.0f).requiresCorrectToolForDrops();
			case "ore_oil_sand" -> Properties.of().strength(3.0f).requiresCorrectToolForDrops();
			case "lantern" -> Properties.of().strength(1.7f).lightLevel(s -> 14); // 0.98 * 15, rounded down
			default -> Properties.of().strength(1.7f);
		};
	}

	private static Block create(String path, Properties p) {
		TCBlockMenu.Layout layout = switch (path) {
			case "distillery" -> TCBlockMenu.DISTILLERY;
			case "hearth_furnace" -> TCBlockMenu.HEARTH_FURNACE;
			case "assembly_table_1", "assembly_table_2", "assembly_table_3" -> TCBlockMenu.ASSEMBLY_TABLE;
			case "train_workbench" -> TCBlockMenu.TRAIN_WORKBENCH;
			default -> null;
		};
		if (layout != null) return new ContainerBlock(p, layout);
		if (path.equals("wind_mill")) return new WindMillBlock(p);
		if (path.equals("switch_stand")) return new SwitchStandBlock(p);
		return FACING.contains(path) ? new FacingBlock(p) : new Block(p);
	}

	public static void register() {
		for (String path : RegistryNames.BLOCKS) {
			Identifier id = Identifier.fromNamespaceAndPath("traincraft", path);
			if (BLOCKS.containsKey(path) || BuiltInRegistries.BLOCK.containsKey(id))
				throw new IllegalStateException("Duplicate block registration: " + id);
			ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
			BLOCKS.put(path, Registry.register(BuiltInRegistries.BLOCK, key, create(path, props(path).setId(key))));
		}
	}
}
