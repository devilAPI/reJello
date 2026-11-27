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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Meteor Client TunnelESP: highlights walkable tunnels around the player.
 * The per-chunk background worker is replaced by a throttled main-thread scan of the local area (the chunk
 * iterator API Jello exposes is not thread-safe here), so results are a little slower to appear but identical.
 */
public class TunnelESP extends Module {
	private static final Direction[] HORIZONTAL = {Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.WEST};
	private static final int MAX_POSITIONS = 1024;
	private static final int WALKABLE = 0;
	private static final int PARTIALLY_BLOCKED = 1;
	private static final int FULLY_BLOCKED = 2;

	private final NumberValue height = addValue(new NumberValue("Height", 0.1, 0.0, 2.0, 0.1));
	private final BooleanValue connected = addValue(new BooleanValue("Connected", true));
	private final ModeValue shape = addValue(new ModeValue("Shape Mode", "Both", "Both", "Lines", "Sides"));
	private final NumberValue range = addValue(new NumberValue("Range", 16, 8, 32, 4));

	private final List<BlockPos> tunnels = new ArrayList<>();
	private final BlockPos.MutableBlockPos scratch = new BlockPos.MutableBlockPos();
	private int timer;

	public TunnelESP() {
		super("Tunnel Esp", 0, Category.RENDER);
	}

	@Override
	public void onDisable() {
		tunnels.clear();
	}

	@EventTarget
	public void onTick(EventTick event) {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (++timer < 60) {
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
		float h = (float) height.getValue();
		int line = 0xFFFFAF19;
		int fill = 0x64FFAF19;

		for (BlockPos pos : tunnels) {
			Vec3 min = new Vec3(pos.getX(), pos.getY(), pos.getZ());
			float[] box = screenBox(event, min, min.add(1, h, 1));
			if (box == null) {
				continue;
			}
			if (sides) {
				Render2D.rect(event.graphics, box[0], box[1], box[2], box[3], fill);
			}
			if (lines) {
				Render2D.rect(event.graphics, box[0], box[1], box[2], box[1] + 1.1f, line);
				Render2D.rect(event.graphics, box[0], box[3] - 1.1f, box[2], box[3], line);
				Render2D.rect(event.graphics, box[0], box[1], box[0] + 1.1f, box[3], line);
				Render2D.rect(event.graphics, box[2] - 1.1f, box[1], box[2], box[3], line);
			}
		}
	}

	private void scan() {
		tunnels.clear();
		BlockPos center = mc.player.blockPosition();
		int radius = (int) range.getValue();
		int minY = Math.max(mc.level.getMinY(), center.getY() - 12);
		int maxY = Math.min(mc.level.getMaxY(), center.getY() + 12);
		Set<BlockPos> positions = new HashSet<>();

		for (int x = center.getX() - radius; x <= center.getX() + radius && positions.size() < MAX_POSITIONS; x++) {
			for (int z = center.getZ() - radius; z <= center.getZ() + radius && positions.size() < MAX_POSITIONS; z++) {
				for (int y = minY; y <= maxY; y++) {
					if (isTunnel(x, y, z)) {
						positions.add(new BlockPos(x, y, z));
						if (positions.size() >= MAX_POSITIONS) {
							break;
						}
					}
				}
			}
		}

		if (connected.getValue()) {
			for (BlockPos pos : positions) {
				for (Direction direction : HORIZONTAL) {
					if (positions.contains(pos.relative(direction))) {
						tunnels.add(pos);
						break;
					}
				}
			}
		} else {
			tunnels.addAll(positions);
		}
	}

	private boolean isTunnel(int x, int y, int z) {
		if (!canWalkIn(x, y, z)) {
			return false;
		}

		int east = sideType(x + 1, y, z);
		int west = sideType(x - 1, y, z);
		int south = sideType(x, y, z + 1);
		int north = sideType(x, y, z - 1);

		if (east == PARTIALLY_BLOCKED || west == PARTIALLY_BLOCKED
			|| south == PARTIALLY_BLOCKED || north == PARTIALLY_BLOCKED) {
			return false;
		}

		return (east == WALKABLE && west == WALKABLE && south == FULLY_BLOCKED && north == FULLY_BLOCKED)
			|| (east == FULLY_BLOCKED && west == FULLY_BLOCKED && south == WALKABLE && north == WALKABLE);
	}

	private int sideType(int x, int y, int z) {
		if (canWalkIn(x, y, z)) {
			return WALKABLE;
		}
		if (!canWalkThrough(x, y, z) && !canWalkThrough(x, y + 1, z)) {
			return FULLY_BLOCKED;
		}
		return PARTIALLY_BLOCKED;
	}

	private boolean canWalkIn(int x, int y, int z) {
		return canWalkOn(x, y - 1, z) && canWalkThrough(x, y, z)
			&& !canWalkThrough(x, y + 2, z) && canWalkThrough(x, y + 1, z);
	}

	private boolean canWalkOn(int x, int y, int z) {
		BlockState state = mc.level.getBlockState(scratch.set(x, y, z));
		if (state.isAir() || !state.getFluidState().isEmpty()) {
			return false;
		}
		return !state.getCollisionShape(mc.level, scratch).isEmpty();
	}

	private boolean canWalkThrough(int x, int y, int z) {
		BlockState state = mc.level.getBlockState(scratch.set(x, y, z));
		if (state.isAir()) {
			return true;
		}
		if (!state.getFluidState().isEmpty()) {
			return false;
		}
		return state.getCollisionShape(mc.level, scratch).isEmpty();
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
