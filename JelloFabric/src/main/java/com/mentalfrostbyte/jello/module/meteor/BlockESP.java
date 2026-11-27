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
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * Meteor Client BlockESP: highlights configured ores through walls.
 * Jello has no 3D render event, so the block AABBs are projected to screen space in {@link EventRender2D}.
 */
public class BlockESP extends Module {
	private static final Set<Block> ORES = Set.of(
		Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE,
		Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
		Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE,
		Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE,
		Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE,
		Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
		Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE,
		Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
		Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE, Blocks.ANCIENT_DEBRIS
	);
	private static final int MAX_RESULTS = 512;

	private final NumberValue range = addValue(new NumberValue("Range", 32, 8, 64, 8));
	private final ModeValue ores = addValue(new ModeValue("Ores", "All", "All", "Diamond", "Nether"));
	private final ModeValue shape = addValue(new ModeValue("Shape Mode", "Lines", "Both", "Lines", "Sides"));
	private final BooleanValue tracers = addValue(new BooleanValue("Tracers", false));

	private final List<BlockPos> found = new ArrayList<>();
	private int timer;

	public BlockESP() {
		super("Block Esp", 0, Category.RENDER);
	}

	@Override
	public void onEnable() {
		timer = 0;
		if (mc.level != null && mc.player != null) {
			scan();
		}
	}

	@Override
	public void onDisable() {
		found.clear();
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
		for (BlockPos pos : found) {
			int color = color(pos);
			float[] box = screenBox(event, Vec3.atLowerCornerOf(pos), Vec3.atLowerCornerOf(pos).add(1, 1, 1));
			if (box == null) {
				continue;
			}
			if (sides) {
				Render2D.rect(event.graphics, box[0], box[1], box[2], box[3], (color & 0x00FFFFFF) | 0x33000000);
			}
			if (lines) {
				Render2D.rect(event.graphics, box[0], box[1], box[2], box[1] + 1.25f, color);
				Render2D.rect(event.graphics, box[0], box[3] - 1.25f, box[2], box[3], color);
				Render2D.rect(event.graphics, box[0], box[1], box[0] + 1.25f, box[3], color);
				Render2D.rect(event.graphics, box[2] - 1.25f, box[1], box[2], box[3], color);
			}
			if (tracers.getValue()) {
				Render2D.line(event.graphics, event.graphics.guiWidth() / 2f, event.graphics.guiHeight() / 2f,
					(box[0] + box[2]) / 2f, (box[1] + box[3]) / 2f, 1f, (color & 0x00FFFFFF) | 0x66000000);
			}
		}
	}

	private void scan() {
		found.clear();
		BlockPos center = mc.player.blockPosition();
		int radius = (int) range.getValue();
		int yRadius = Math.min(32, radius);
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for (int x = -radius; x <= radius && found.size() < MAX_RESULTS; x++) {
			for (int z = -radius; z <= radius && found.size() < MAX_RESULTS; z++) {
				for (int y = -yRadius; y <= yRadius; y++) {
					pos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
					if (matches(mc.level.getBlockState(pos).getBlock())) {
						found.add(pos.immutable());
						if (found.size() >= MAX_RESULTS) {
							break;
						}
					}
				}
			}
		}
	}

	private boolean matches(Block block) {
		if (!ORES.contains(block)) {
			return false;
		}
		if (ores.is("Diamond")) {
			return block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE
				|| block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE
				|| block == Blocks.ANCIENT_DEBRIS;
		}
		if (ores.is("Nether")) {
			return block == Blocks.NETHER_GOLD_ORE || block == Blocks.NETHER_QUARTZ_ORE
				|| block == Blocks.ANCIENT_DEBRIS;
		}
		return true;
	}

	private int color(BlockPos pos) {
		Block block = mc.level.getBlockState(pos).getBlock();
		if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) return 0xFF55FFFF;
		if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) return 0xFF55FF55;
		if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE || block == Blocks.NETHER_GOLD_ORE) return 0xFFFFD700;
		if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) return 0xFFD8AF93;
		if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) return 0xFFFF8C42;
		if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) return 0xFFFF3333;
		if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) return 0xFF3366FF;
		if (block == Blocks.NETHER_QUARTZ_ORE) return 0xFFFFFFFF;
		if (block == Blocks.ANCIENT_DEBRIS) return 0xFF8B5A2B;
		return 0xFF9E9E9E;
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
