package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.Render2D;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * Wurst CaveFinder: highlights cave air around the player.
 *
 * <p>Jello has no 3D world render event, so the blocks are projected to 2D boxes like BlockESP.
 * The original caches chunk results and uploads a vertex buffer; this version scans on a timer.
 * A full port needs a world-render event / vertex buffer hook (no mixin is required for the scan).</p>
 */
public class CaveFinder extends Module {
	private final NumberValue range = addValue(new NumberValue("Range", 20, 8, 32, 1));
	private final NumberValue limit = addValue(new NumberValue("Limit", 256, 64, 1024, 64));
	private final NumberValue opacity = addValue(new NumberValue("Opacity", 0.5, 0.1, 1, 0.05));

	private final List<BlockPos> found = new ArrayList<>();
	private int timer;

	public CaveFinder() {
		super("Cave Finder", 0, Category.RENDER);
	}

	@Override
	public void onEnable() {
		found.clear();
		timer = 0;
		if (mc.player != null && mc.level != null) {
			scan();
		}
	}

	@Override
	public void onDisable() {
		found.clear();
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (++timer < 40) {
			return;
		}
		timer = 0;
		scan();
	}

	private void scan() {
		found.clear();
		int radius = (int) range.getValue();
		int yRadius = Math.min(24, radius);
		BlockPos origin = mc.player.blockPosition();
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				for (int y = -yRadius; y <= yRadius; y++) {
					BlockPos pos = origin.offset(x, y, z);
					if (mc.level.getBlockState(pos).is(Blocks.CAVE_AIR)) {
						found.add(pos.immutable());
					}
				}
			}
		}
		double px = mc.player.getX();
		double py = mc.player.getY();
		double pz = mc.player.getZ();
		found.sort(Comparator.comparingDouble(pos -> Vec3.atCenterOf(pos).distanceToSqr(px, py, pz)));
		int max = (int) limit.getValue();
		if (found.size() > max) {
			found.subList(max, found.size()).clear();
		}
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || mc.player == null || mc.level == null || found.isEmpty()) {
			return;
		}
		int alpha = (int) (opacity.getValue() * 255);
		int fill = (alpha << 24) | 0xFF3333;
		int line = 0xFFFF5555;
		for (BlockPos pos : found) {
			Vec3 min = Vec3.atLowerCornerOf(pos);
			float[] box = screenBox(event, min, min.add(1, 1, 1));
			if (box == null) {
				continue;
			}
			Render2D.rect(event.graphics, box[0], box[1], box[2], box[3], fill);
			Render2D.rect(event.graphics, box[0], box[1], box[2], box[1] + 1f, line);
			Render2D.rect(event.graphics, box[0], box[3] - 1f, box[2], box[3], line);
			Render2D.rect(event.graphics, box[0], box[1], box[0] + 1f, box[3], line);
			Render2D.rect(event.graphics, box[2] - 1f, box[1], box[2], box[3], line);
		}
	}

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
		if (x1 < 0 || y1 < 0 || x0 > guiW || y0 > guiH || x1 - x0 < 1.5f || y1 - y0 < 1.5f) {
			return null;
		}
		return new float[] {Math.max(0, x0), Math.max(0, y0), Math.min(guiW, x1), Math.min(guiH, y1)};
	}
}
