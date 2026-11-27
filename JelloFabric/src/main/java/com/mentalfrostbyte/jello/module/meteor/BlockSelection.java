package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.Render2D;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Meteor Client block selection. Rendered through the 2D overlay by projecting the block shape to the screen,
 * since this build has no 3D render event; the vanilla outline itself is not replaced.
 */
public class BlockSelection extends Module {
	private final BooleanValue advanced = addValue(new BooleanValue("Advanced", true));
	private final BooleanValue singleSide = addValue(new BooleanValue("Single Side", false));
	private final ModeValue shapeMode = addValue(new ModeValue("Shape Mode", "Both", "Both", "Lines", "Sides"));
	private final BooleanValue hideInside = addValue(new BooleanValue("Hide When Inside Block", true));

	public BlockSelection() {
		super("Block Selection", 0, Category.RENDER);
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || mc.level == null || !(mc.hitResult instanceof BlockHitResult hit)
			|| hit.getType() == HitResult.Type.MISS) {
			return;
		}
		if (hideInside.getValue() && hit.isInside()) {
			return;
		}
		BlockPos pos = hit.getBlockPos();
		VoxelShape shape = mc.level.getBlockState(pos).getShape(mc.level, pos);
		if (shape.isEmpty()) {
			return;
		}
		if (singleSide.getValue()) {
			draw(event.graphics, pos, faceBox(shape.bounds(), hit.getDirection()));
		} else if (advanced.getValue()) {
			for (AABB box : shape.toAabbs()) {
				draw(event.graphics, pos, box);
			}
		} else {
			draw(event.graphics, pos, shape.bounds());
		}
	}

	private void draw(GuiGraphicsExtractor g, BlockPos pos, AABB box) {
		float[] rect = project(g, pos, box);
		if (rect == null) {
			return;
		}
		float x0 = rect[0], y0 = rect[1], x1 = rect[2], y1 = rect[3];
		if (!shapeMode.is("Lines")) {
			Render2D.rect(g, x0, y0, x1, y1, 0x32FFFFFF);
		}
		if (!shapeMode.is("Sides")) {
			Render2D.rect(g, x0, y0, x1, y0 + 1.5F, 0xFFFFFFFF);
			Render2D.rect(g, x0, y1 - 1.5F, x1, y1, 0xFFFFFFFF);
			Render2D.rect(g, x0, y0, x0 + 1.5F, y1, 0xFFFFFFFF);
			Render2D.rect(g, x1 - 1.5F, y0, x1, y1, 0xFFFFFFFF);
		}
	}

	private static AABB faceBox(AABB box, Direction side) {
		double t = 0.001;
		return switch (side) {
			case UP -> new AABB(box.minX, box.maxY - t, box.minZ, box.maxX, box.maxY, box.maxZ);
			case DOWN -> new AABB(box.minX, box.minY, box.minZ, box.maxX, box.minY + t, box.maxZ);
			case NORTH -> new AABB(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ + t);
			case SOUTH -> new AABB(box.minX, box.minY, box.maxZ - t, box.maxX, box.maxY, box.maxZ);
			case WEST -> new AABB(box.minX, box.minY, box.minZ, box.minX + t, box.maxY, box.maxZ);
			case EAST -> new AABB(box.maxX - t, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
		};
	}

	private float[] project(GuiGraphicsExtractor g, BlockPos pos, AABB box) {
		float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
		for (int i = 0; i < 8; i++) {
			double x = pos.getX() + ((i & 1) == 0 ? box.minX : box.maxX);
			double y = pos.getY() + (((i >> 1) & 1) == 0 ? box.minY : box.maxY);
			double z = pos.getZ() + (((i >> 2) & 1) == 0 ? box.minZ : box.maxZ);
			Vec3 p = mc.gameRenderer.projectPointToScreen(new Vec3(x, y, z));
			if (p.z < -1 || p.z > 1) {
				return null;
			}
			float sx = (float) ((p.x * 0.5 + 0.5) * g.guiWidth());
			float sy = (float) ((-p.y * 0.5 + 0.5) * g.guiHeight());
			minX = Math.min(minX, sx);
			minY = Math.min(minY, sy);
			maxX = Math.max(maxX, sx);
			maxY = Math.max(maxY, sy);
		}
		return new float[] {minX, minY, maxX, maxY};
	}
}
