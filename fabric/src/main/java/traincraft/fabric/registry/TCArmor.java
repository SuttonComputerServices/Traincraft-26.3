/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("TCArmor.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.registry;

import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

/** Armor materials for the Traincraft suits. Protection values are the 1.12 int[] order: boots, legs, chest, helmet. */
public final class TCArmor {
	private TCArmor() {}

	/** Durability is the 1.12 multiplier (5, or 70 for the composite suit) times 11, the leather helmet base. */
	public static final ArmorMaterial HAT = material(55, 1, 2, 2, 1, 25, 0f, ItemTags.WOOL, "hat");
	public static final ArmorMaterial JACKET = material(55, 1, 2, 2, 1, 25, 0f, ItemTags.WOOL, "jacket");
	public static final ArmorMaterial OVERALLS = material(55, 1, 2, 2, 1, 25, 0f, ItemTags.WOOL, "overalls");
	public static final ArmorMaterial TICKETMAN = material(55, 1, 2, 2, 1, 25, 0f, ItemTags.WOOL, "ticketman");
	public static final ArmorMaterial DRIVER = material(55, 1, 2, 2, 1, 25, 0f, ItemTags.WOOL, "driver");
	public static final ArmorMaterial COMPOSITE = material(770, 2, 6, 5, 2, 50, 2f, ItemTags.DIAMOND_TOOL_MATERIALS, "composite_suit");

	/** Adds the armor component for an armor path; other paths return the properties unchanged. */
	public static Item.Properties apply(String path, Item.Properties props) {
		return switch (path) {
			case "armor_hat" -> props.humanoidArmor(HAT, ArmorType.HELMET);
			case "armor_jacket" -> props.humanoidArmor(JACKET, ArmorType.CHESTPLATE);
			case "armor_overalls" -> props.humanoidArmor(OVERALLS, ArmorType.CHESTPLATE);
			case "armor_ticketman_hat" -> props.humanoidArmor(TICKETMAN, ArmorType.HELMET);
			case "armor_ticketman_jacket" -> props.humanoidArmor(TICKETMAN, ArmorType.CHESTPLATE);
			case "armor_ticketman_pants" -> props.humanoidArmor(TICKETMAN, ArmorType.LEGGINGS);
			case "armor_driver_hat" -> props.humanoidArmor(DRIVER, ArmorType.HELMET);
			case "armor_driver_jacket" -> props.humanoidArmor(DRIVER, ArmorType.CHESTPLATE);
			case "armor_driver_pants" -> props.humanoidArmor(DRIVER, ArmorType.LEGGINGS);
			case "armor_composite_suit_head" -> props.humanoidArmor(COMPOSITE, ArmorType.HELMET);
			case "armor_composite_suit_chest" -> props.humanoidArmor(COMPOSITE, ArmorType.CHESTPLATE);
			case "armor_composite_suit_legs" -> props.humanoidArmor(COMPOSITE, ArmorType.LEGGINGS);
			case "armor_composite_suit_feet" -> props.humanoidArmor(COMPOSITE, ArmorType.BOOTS);
			default -> props;
		};
	}

	private static ArmorMaterial material(int durability, int boots, int legs, int chest, int helmet, int enchant,
			float toughness, net.minecraft.tags.TagKey<Item> repair, String asset) {
		ResourceKey<EquipmentAsset> key = ResourceKey.create(EquipmentAssets.ROOT_ID,
			Identifier.fromNamespaceAndPath("traincraft", asset));
		return new ArmorMaterial(durability,
			Map.of(ArmorType.BOOTS, boots, ArmorType.LEGGINGS, legs, ArmorType.CHESTPLATE, chest, ArmorType.HELMET, helmet),
			enchant, SoundEvents.ARMOR_EQUIP_GENERIC, toughness, 0f, repair, key);
	}
}
