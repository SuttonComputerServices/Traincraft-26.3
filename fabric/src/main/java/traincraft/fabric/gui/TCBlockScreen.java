/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("TCBlockScreen.java") is part of the Traincraft mod for Minecraft.
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

/** Screen for the item containers. The 1.12 art is 176 by 166, the same size as the other inventory screens here. */
public class TCBlockScreen extends AbstractContainerScreen<TCBlockMenu> {
	private final Identifier texture;

	public TCBlockScreen(TCBlockMenu menu, Inventory inventory, Component title, Identifier texture) {
		super(menu, inventory, title, 176, 166);
		this.texture = texture;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(g, mouseX, mouseY, partialTick);
		g.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);
	}
}
