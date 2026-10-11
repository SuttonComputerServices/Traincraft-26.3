/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("SteamSmallScreen.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class SteamSmallScreen extends AbstractContainerScreen<SteamSmallMenu> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("traincraft", "textures/gui/gui_loco_steam.png");

	public SteamSmallScreen(SteamSmallMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, 176, 166);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(g, mouseX, mouseY, partialTick);
		g.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);
	}
}
