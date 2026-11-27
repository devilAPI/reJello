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
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Meteor Client HoleESP: finds safe (obsidian/bedrock) holes around the player and outlines them.
 * Holes are stored in world space and projected to screen space because Jello has no 3D render event.
 */
public class HoleESP extends Module {
	private static final Direction[] SIDES = {Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
	private static final int MAX_HOLES = 512;

	/** type: 0 = bedrock, 1 = obsidian, 2 = mixed. */
	private record Hole(BlockPos pos, int type) {
	}

	private final NumberValue horizontalRadius = addValue(new NumberValue("Horizontal Radius", 10, 0, 32, 1));
	private final NumberValue verticalRadius = addValue(new NumberValue("Vertical Radius", 5, 0, 32, 1));
	private final NumberValue minHeight = addValue(new NumberValue("Min Height", 3, 1, 10, 1));
	private final BooleanValue doubles = addValue(new BooleanValue("Doubles", true));
	private final BooleanValue ignoreOwn = addValue(new BooleanValue("Ignore Own", false));
	private final BooleanValue webs = addValue(new BooleanValue("Webs", false));
	private final ModeValue shape = addValue(new ModeValue("Shape Mode", "Both", "Both", "Lines", "Sides"));
	private final NumberValue height = addValue(new NumberValue("Height", 0.2, 0.0, 2.0, 0.1));
	private final BooleanValue topQuad = addValue(new BooleanValue("Top Quad", true));
	private final BooleanValue bottomQuad = addValue(new BooleanValue("Bottom Quad", false));

	private final List<Hole> holes = new ArrayList<>();
	private int timer;

	public HoleESP() {
		super("Hole Esp", 0, Category.RENDER);
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
		if (++timer < 20) {
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

		for (Hole hole : holes) {
			BlockPos pos = hole.pos();
			int line;
			int fill;
			switch (hole.type()) {
				case 0 -> {
					line = 0xFF64FF00;
					fill = 0x6464FF00;
				}
				case 1 -> {
					line = 0xFFFF0000;
					fill = 0x64FF0000;
				}
				default -> {
					line = 0xFFFF7F00;
					fill = 0x64FF7F00;
				}
			}

			Vec3 min = new Vec3(pos.getX(), pos.getY(), pos.getZ());
			float[] box = screenBox(event, min, min.add(1, h, 1));
			if (box == null) {
				continue;
			}
			if (sides) {
				Render2D.rect(event.graphics, box[0], box[1], box[2], box[3], fill);
			}
			if (lines) {
				if (topQuad.getValue()) {
					Render2D.rect(event.graphics, box[0], box[1], box[2], box[1] + 1.25f, line);
				}
				if (bottomQuad.getValue()) {
					Render2D.rect(event.graphics, box[0], box[3] - 1.25f, box[2], box[3], line);
				}
				Render2D.rect(event.graphics, box[0], box[1], box[0] + 1.25f, box[3], line);
				Render2D.rect(event.graphics, box[2] - 1.25f, box[1], box[2], box[3], line);
			}
		}
	}

	private void scan() {
		holes.clear();
		BlockPos center = mc.player.blockPosition();
		int hRadius = (int) horizontalRadius.getValue();
		int vRadius = (int) verticalRadius.getValue();

		for (int x = -hRadius; x <= hRadius && holes.size() < MAX_HOLES; x++) {
			for (int z = -hRadius; z <= hRadius && holes.size() < MAX_HOLES; z++) {
				for (int y = -vRadius; y <= vRadius; y++) {
					Hole hole = findHole(new BlockPos(center.getX() + x, center.getY() + y, center.getZ() + z));
					if (hole != null) {
						holes.add(hole);
						if (holes.size() >= MAX_HOLES) {
							break;
						}
					}
				}
			}
		}
	}

	private Hole findHole(BlockPos pos) {
		if (!validHole(pos)) {
			return null;
		}

		int bedrock = 0;
		int obsidian = 0;
		Direction air = null;

		for (Direction direction : SIDES) {
			BlockPos offset = pos.relative(direction);
			BlockState state = mc.level.getBlockState(offset);

			if (isBedrock(state, offset)) {
				bedrock++;
			} else if (isObsidian(state)) {
				obsidian++;
			} else if (direction == Direction.DOWN || !doubles.getValue() || air != null || !validHole(offset)) {
				return null;
			} else {
				int extraBedrock = 0;
				int extraObsidian = 0;
				boolean ok = true;

				for (Direction other : Direction.values()) {
					if (other == direction.getOpposite() || other == Direction.UP) {
						continue;
					}
					BlockPos adjacent = offset.relative(other);
					BlockState adjacentState = mc.level.getBlockState(adjacent);

					if (isBedrock(adjacentState, adjacent)) {
						extraBedrock++;
					} else if (isObsidian(adjacentState)) {
						extraObsidian++;
					} else {
						ok = false;
						break;
					}
				}

				if (!ok) {
					return null;
				}

				bedrock += extraBedrock;
				obsidian += extraObsidian;
				air = direction;
			}
		}

		if (bedrock + obsidian == 5 && air == null) {
			return new Hole(pos, type(bedrock, obsidian));
		}
		if (bedrock + obsidian == 8 && air != null) {
			return new Hole(pos, type(bedrock, obsidian));
		}
		return null;
	}

	private int type(int bedrock, int obsidian) {
		if (obsidian == 0) {
			return 0;
		}
		if (bedrock == 0) {
			return 1;
		}
		return 2;
	}

	private boolean validHole(BlockPos pos) {
		if (ignoreOwn.getValue() && mc.player.blockPosition().equals(pos)) {
			return false;
		}

		BlockState state = mc.level.getBlockState(pos);
		if (!webs.getValue() && state.is(Blocks.COBWEB)) {
			return false;
		}
		if (!state.getCollisionShape(mc.level, pos).isEmpty()) {
			return false;
		}

		int required = (int) minHeight.getValue();
		for (int i = 1; i < required; i++) {
			BlockPos above = pos.above(i);
			if (!mc.level.getBlockState(above).getCollisionShape(mc.level, above).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private boolean isBedrock(BlockState state, BlockPos pos) {
		return !state.getCollisionShape(mc.level, pos).isEmpty() && state.getBlock().defaultDestroyTime() < 0;
	}

	private boolean isObsidian(BlockState state) {
		Block block = state.getBlock();
		return block.getExplosionResistance() >= 600 && block.defaultDestroyTime() >= 0;
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
