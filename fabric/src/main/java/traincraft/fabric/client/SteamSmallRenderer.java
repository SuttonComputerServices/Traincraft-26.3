/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("SteamSmallRenderer.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import traincraft.fabric.entity.SteamSmallEntity;

public class SteamSmallRenderer extends EntityRenderer<SteamSmallEntity, SteamSmallRenderer.State> {
	public static final ModelLayerLocation LAYER =
		new ModelLayerLocation(Identifier.fromNamespaceAndPath("traincraft", "steam_small"), "main");
	// Texture from the 1.12 source, lowercased because Identifier paths must be lowercase.
	private static final Identifier TEXTURE =
		Identifier.fromNamespaceAndPath("traincraft", "textures/trains/locosmallsteameu.png");

	// Matches the 1.12 LocomotiveSteamSmall.getModelScale.
	private static final float MODEL_SCALE = 0.8f;

	public static class State extends EntityRenderState {
		float yRot;
	}

	private final SteamSmallModel model;

	public SteamSmallRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
		this.model = new SteamSmallModel(ctx.bakeLayer(LAYER));
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(SteamSmallEntity entity, State state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		state.yRot = entity.getYRot();
	}

	@Override
	public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		pose.pushPose();
		pose.rotateDegrees(Axis.YP, -state.yRot - 45.0f);
		// Source (TMT) boxes extend upward from y=0; vanilla model space extends downward. Visual check pending.
		pose.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
		pose.scale(-1.0f, -1.0f, 1.0f);
		collector.submitModel(model, state, pose, TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
		pose.popPose();
		super.submit(state, pose, collector, camera);
	}
}
