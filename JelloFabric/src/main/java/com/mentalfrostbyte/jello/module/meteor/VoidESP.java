package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.event.events.EventTick;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.Render2D;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Meteor Client VoidESP: highlights holes in the bedrock layer (and the nether roof) that lead to the void.
 * Positions are scanned periodically and drawn as projected boxes.
 */
public class VoidESP extends Module {
	private static final int MAX_HOLES = 2048;

	private final BooleanValue airOnly = addValue(new BooleanValue("Air Only", false));
	private final NumberValue horizontalRadius = addValue(new NumberValue("Horizontal Radius", 64, 0, 128, 8));
	private final NumberValue holeHeight = addValue(new NumberValue("Hole Height", 1, 1, 5, 1));
	private final BooleanValue netherRoof = addValue(new BooleanValue("Nether Roof", true));
	private final ModeValue shape = addValue(new ModeValue("Shape Mode", "Both", "Both", "Lines", "Sides"));

	private final List<BlockPos> holes = new ArrayList<>();
	private int timer;

	public VoidESP() {
		super("Void Esp", 0, Category.RENDER);
	}

	@Override
	public void onDisable() {
		holes.clear();
	}

	@EventTarget
	public void onTick(EventTick event) {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (++timer < 100) {
			return;
		}
		timer = 0;
		scan();
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		boolean lines = !shape.is("Sides");
		boolean sides = !shape.is("Lines");
		int line = 0xFFE119FF;
		int fill = 0x32E11919;

		for (BlockPos pos : holes) {
			Vec3 min = new Vec3(pos.getX(), pos.getY(), pos.getZ());
			float[] box = screenBox(event, min, min.add(1, 1, 1));
			if (box == null) {
				continue;
			}
			if (sides) {
				Render2D.rect(event.graphics, box[0], box[1], box[2], box[3], fill);
			}
			if (lines) {
				Render2D.rect(event.graphics, box[0], box[1], box[2], box[1] + 1.25f, line);
				Render2D.rect(event.graphics, box[0], box[3] - 1.25f, box[2], box[3], line);
				Render2D.rect(event.graphics, box[0], box[1], box[0] + 1.25f, box[3], line);
				Render2D.rect(event.graphics, box[2] - 1.25f, box[1], box[2], box[3], line);
			}
		}
	}

	private void scan() {
		holes.clear();
		if (mc.level.dimension() == Level.END) {
			return;
		}

		BlockPos center = mc.player.blockPosition();
		int radius = (int) horizontalRadius.getValue();
		int height = (int) holeHeight.getValue();
		int minY = mc.level.getMinY();
		boolean nether = mc.level.dimension() == Level.NETHER;

		for (int x = center.getX() - radius; x <= center.getX() + radius && holes.size() < MAX_HOLES; x++) {
			for (int z = center.getZ() - radius; z <= center.getZ() + radius && holes.size() < MAX_HOLES; z++) {
				if (isHole(x, minY, z, height)) {
					holes.add(new BlockPos(x, minY, z));
				}
				if (netherRoof.getValue() && nether && isHole(x, 127, z, height)) {
					holes.add(new BlockPos(x, 127, z));
				}
				if (holes.size() >= MAX_HOLES) {
					break;
				}
			}
		}
	}

	private boolean isHole(int x, int y, int z, int height) {
		for (int i = 0; i < height; i++) {
			BlockState state = mc.level.getBlockState(new BlockPos(x, y + i, z));
			if (airOnly.getValue()) {
				if (!state.isAir()) {
					return false;
				}
			} else if (state.is(Blocks.BEDROCK)) {
				return false;
			}
		}
		return true;
	}

	/** Projects a world-space AABB to screen space; returns {minX, minY, maxX, maxY} or null when off-screen. */
	private float[] screenBox(EventRender2D event, Vec3 min, Vec3 max) {
		float guiW = event.graphics.guiWidth();
		float guiH = event.graphics.guiHeight();
		float x0 = Float.MAX_VALUE, y0 = Float.MAX_VALUE, x1 = -Float.MAX_VALUE, y1 = -Float.MAX_VALUE;

		for (int i = 0; i < 8; i++) {
			Vec3 p = mc.gameRenderer.projectPointToScreen(new Vec3(
				(i & 1) == 0 ? min.x : max.x,
				(i & 2) == 0 ? min.y : max.y,
				(i & 4) == 0 ? min.z : max.z));
			if (p.z < -1 || p.z > 1 || Double.isNaN(p.x) || Double.isNaN(p.y)) {
				return null;
			}
			float sx = (float) ((p.x * 0.5 + 0.5) * guiW);
			float sy = (float) ((-p.y * 0.5 + 0.5) * guiH);
			x0 = Math.min(x0, sx);
			y0 = Math.min(y0, sy);
			x1 = Math.max(x1, sx);
			y1 = Math.max(y1, sy);
		}

		if (x1 < 0 || y1 < 0 || x0 > guiW || y0 > guiH) {
			return null;
		}
		x0 = Math.max(0, x0);
		y0 = Math.max(0, y0);
		x1 = Math.min(guiW, x1);
		y1 = Math.min(guiH, y1);
		if (x1 - x0 < 1.5f || y1 - y0 < 1.5f) {
			return null;
		}
		return new float[]{x0, y0, x1, y1};
	}
}
