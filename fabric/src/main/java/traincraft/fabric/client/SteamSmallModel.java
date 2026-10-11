/*
 * Traincraft
 * Copyright (c) 2011-2020.
 *
 * This file ("SteamSmallModel.java") is part of the Traincraft mod for Minecraft.
 * It is created by all people that are listed with @author below.
 * It is distributed under LGPL-v3.0.
 * You can find the source code at https://github.com/Traincraft/Traincraft
 */

package traincraft.fabric.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

/** Body of the small steam locomotive. Boxes are generated from jtmt/train/steam/small.jtmt. */
public class SteamSmallModel extends EntityModel<EntityRenderState> {
	/** x, y, z, width, height, depth, texture u, texture v. */
	private static final float[][] BOXES = {
		{-3.0f, 8.0f, 8.0f, 6.0f, 4.0f, 8.0f, 0, 0},
		{-2.0f, 24.0f, 12.0f, 4.0f, 12.0f, 4.0f, 43, 112},
		{-10.0f, 4.0f, 8.0f, 20.0f, 4.0f, 12.0f, 64, 0},
		{-10.0f, 12.0f, -1.0f, 20.0f, 24.0f, 1.0f, 0, 103},
		{6.0f, 5.0f, -11.0f, 1.0f, 1.0f, 13.0f, 60, 42},
		{9.0f, 24.0f, -9.0f, 1.0f, 12.0f, 8.0f, 18, 68},
		{4.0f, 0.0f, -4.0f, 2.0f, 8.0f, 8.0f, 0, 24},
		{-10.0f, 24.0f, -9.0f, 1.0f, 12.0f, 8.0f, 0, 68},
		{4.0f, 0.0f, -16.0f, 2.0f, 8.0f, 8.0f, 0, 24},
		{-10.0f, 12.0f, -20.0f, 1.0f, 12.0f, 19.0f, 88, 26},
		{-3.0f, 24.0f, 3.0f, 6.0f, 6.0f, 6.0f, 0, 12},
		{9.0f, 12.0f, -20.0f, 1.0f, 12.0f, 19.0f, 88, 57},
		{-6.0f, 0.0f, -16.0f, 2.0f, 8.0f, 8.0f, 0, 40},
		{-6.0f, 0.0f, -4.0f, 2.0f, 8.0f, 8.0f, 0, 40},
		{-3.0f, 22.0f, 16.0f, 6.0f, 8.0f, 6.0f, 39, 0},
		{-7.0f, 5.0f, -14.0f, 1.0f, 1.0f, 13.0f, 60, 42},
		{-6.0f, 12.0f, -4.0f, 12.0f, 12.0f, 22.0f, 60, 94},
		{-10.0f, 8.0f, -22.0f, 20.0f, 4.0f, 30.0f, 7, 59},
		{-10.0f, 36.0f, -20.0f, 20.0f, 1.0f, 20.0f, 0, 37}
	};

	public SteamSmallModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition body = mesh.getRoot().addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
		for (int i = 0; i < BOXES.length; i++) {
			float[] b = BOXES[i];
			body.addOrReplaceChild("box" + i,
				CubeListBuilder.create().texOffs((int) b[6], (int) b[7]).addBox(b[0], b[1], b[2], b[3], b[4], b[5]),
				PartPose.ZERO);
		}
		return LayerDefinition.create(mesh, 128, 128);
	}
}
