/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("AssetGameTest.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import traincraft.common.registry.RegistryNames;

public class AssetGameTest {
	@GameTest
	public void everyRegisteredIdHasDefinitionAndModel(GameTestHelper helper) throws IOException {
		for (String id : RegistryNames.BLOCKS) {
			require(helper, "items/" + id + ".json", "item definition " + id);
			require(helper, "blockstates/" + id + ".json", "blockstate " + id);
			checkTextures(helper, "models/block/" + id + ".json");
		}
		for (String id : RegistryNames.ITEMS) {
			require(helper, "items/" + id + ".json", "item definition " + id);
			checkTextures(helper, "models/item/" + id + ".json");
		}
		helper.succeed();
	}

	// The gametest server only loads data packs, so assets are read from the classpath.
	private static void require(GameTestHelper helper, String path, String what) {
		helper.assertTrue(AssetGameTest.class.getClassLoader().getResource("assets/traincraft/" + path) != null,
			net.minecraft.network.chat.Component.literal("missing " + what + " (" + path + ")"));
	}

	/** Every traincraft: texture referenced by the model must exist as textures/<path>.png. */
	private static void checkTextures(GameTestHelper helper, String modelPath) throws IOException {
		JsonObject model;
		try (var in = AssetGameTest.class.getClassLoader().getResourceAsStream("assets/traincraft/" + modelPath);
				BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
			model = JsonParser.parseReader(r).getAsJsonObject();
		}
		if (!model.has("textures")) return;
		for (var entry : model.getAsJsonObject("textures").entrySet()) {
			JsonElement v = entry.getValue();
			if (!v.isJsonPrimitive()) continue;
			String s = v.getAsString();
			if (!s.startsWith("traincraft:")) continue;
			require(helper, "textures/" + s.substring("traincraft:".length()) + ".png", "texture " + s + " used by " + modelPath);
		}
	}
}
