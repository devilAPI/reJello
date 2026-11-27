package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.event.events.EventTick;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.Render2D;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * Meteor Client CityESP: highlights the block next to a nearby player that can be broken to "city" them.
 * Ported from EntityUtils.getCityBlock + TargetUtils (nearest player in interaction range).
 */
public class CityESP extends Module {
	private static final Set<Block> CITY_BLOCKS = Set.of(
		Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.NETHERITE_BLOCK,
		Blocks.RESPAWN_ANCHOR, Blocks.ANCIENT_DEBRIS
	);
	private static final Direction[] HORIZONTAL = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

	private final ModeValue shape = addValue(new ModeValue("Shape Mode", "Both", "Both", "Lines", "Sides"));
	private final NumberValue range = addValue(new NumberValue("Range", 6, 1, 12, 1));

	private BlockPos target;
	private int timer;

	public CityESP() {
		super("City Esp", 0, Category.RENDER);
	}

	@Override
	public void onDisable() {
		target = null;
	}

	@EventTarget
	public void onTick(EventTick event) {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (++timer < 5) {
			return;
		}
		timer = 0;
		updateTarget();
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || target == null || mc.player == null || mc.level == null) {
			return;
		}
		boolean lines = !shape.is("Sides");
		boolean sides = !shape.is("Lines");
		int line = 0xFFE10000;
		int fill = 0x4BE10000;

		Vec3 min = new Vec3(target.getX(), target.getY(), target.getZ());
		float[] box = screenBox(event, min, min.add(1, 1, 1));
		if (box == null) {
			return;
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

	private void updateTarget() {
		Player best = null;
		double bestDistance = (double) range.getValue() * range.getValue();

		for (Player player : mc.level.players()) {
			if (player == mc.player || !player.isAlive()) {
				continue;
			}
			double distance = mc.player.distanceToSqr(player);
			if (distance < bestDistance) {
				bestDistance = distance;
				best = player;
			}
		}

		target = best == null ? null : cityBlock(best);
	}

	private BlockPos cityBlock(Player player) {
		BlockPos best = null;
		double bestDistance = Double.MAX_VALUE;

		for (Direction direction : HORIZONTAL) {
			BlockPos pos = player.blockPosition().relative(direction);
			if (!CITY_BLOCKS.contains(mc.level.getBlockState(pos).getBlock())) {
				continue;
			}
			double distance = mc.player.blockPosition().distSqr(pos);
			if (distance < bestDistance) {
				bestDistance = distance;
				best = pos;
			}
		}
		return best;
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
