/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("RegistryNamesTest.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.common.registry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class RegistryNamesTest {
	private static final Pattern VALID = Pattern.compile("[a-z0-9/._-]+");

	@Test
	void namesAreIdentifierSafeAndUnique() {
		List<String> all = new java.util.ArrayList<>(RegistryNames.BLOCKS);
		all.addAll(RegistryNames.ITEMS);
		all.addAll(RegistryNames.ENTITIES);
		assertTrue(new HashSet<>(all).size() == all.size(), "duplicate registry path");
		for (String name : all) assertTrue(VALID.matcher(name).matches(), "invalid path: " + name);
	}

	@Test
	void blockItemsAndOthersDoNotCollide() {
		for (String block : RegistryNames.BLOCKS) assertFalse(RegistryNames.ITEMS.contains(block), block);
	}
}
