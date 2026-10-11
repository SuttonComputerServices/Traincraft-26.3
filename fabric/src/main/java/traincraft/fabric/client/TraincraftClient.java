/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("TraincraftClient.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import traincraft.fabric.gui.SteamSmallMenu;
import traincraft.fabric.gui.SteamSmallScreen;
import traincraft.fabric.registry.TCEntities;

public class TraincraftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModelLayerRegistry.registerModelLayer(SteamSmallRenderer.LAYER, SteamSmallModel::createBodyLayer);
		EntityRendererRegistry.register(TCEntities.steamSmall(), SteamSmallRenderer::new);
		MenuScreens.register(SteamSmallMenu.type(), SteamSmallScreen::new);
	}
}
