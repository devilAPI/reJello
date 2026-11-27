package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.Render2D;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import java.util.SortedSet;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Meteor Client break indicators. This build only exposes a 2D render event, so the block box is projected to the
 * screen and drawn as a growing rectangle instead of a 3D box. Other players' progress comes from the level's
 * destruction map; the local player's progress comes from {@code MultiPlayerGameMode}.
 */
public class BreakIndicators extends Module {
	private final ModeValue shapeMode = addValue(new ModeValue("Shape Mode", "Both", "Both", "Lines", "Sides"));
	private final BooleanValue ownMining = addValue(new BooleanValue("Own Mining", true));
	private final BooleanValue othersMining = addValue(new BooleanValue("Others Mining", true));

	public BreakIndicators() {
		super("Break Indicators", 0, Category.RENDER);
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameRenderer == null) {
			return;
		}
		GuiGraphicsExtractor graphics = event.graphics;
		BlockPos ownPos = null;
		if (ownMining.getValue() && mc.gameMode.isDestroying()
			&& mc.hitResult instanceof BlockHitResult hit && hit.getType() != HitResult.Type.MISS) {
			ownPos = hit.getBlockPos();
			float progress = Math.max(0.05F, Math.min(1.0F, mc.gameMode.getDestroyStage() / 9.0F));
			renderBlock(graphics, ownPos, mc.level.getBlockState(ownPos).getShape(mc.level, ownPos).bounds(), progress);
		}
		if (!othersMining.getValue()) {
			return;
		}
		for (SortedSet<BlockDestructionProgress> set : mc.level.destructionProgress().values()) {
			if (set.isEmpty()) {
				continue;
			}
			BlockDestructionProgress info = set.last();
			BlockPos pos = info.getPos();
			if (pos.equals(ownPos)) {
				continue;
			}
			float progress = Math.max(0.05F, Math.min(1.0F, (info.getProgress() + 1) / 9.0F));
			renderBlock(graphics, pos, mc.level.getBlockState(pos).getShape(mc.level, pos).bounds(), progress);
		}
	}

	private void renderBlock(GuiGraphicsExtractor g, BlockPos pos, AABB box, float progress) {
		float[] rect = project(g, pos, box);
		if (rect == null) {
			return;
		}
		float cx = (rect[0] + rect[2]) / 2.0F;
		float cy = (rect[1] + rect[3]) / 2.0F;
		float hw = (rect[2] - rect[0]) / 2.0F * progress;
		float hh = (rect[3] - rect[1]) / 2.0F * progress;
		float x0 = cx - hw, x1 = cx + hw, y0 = cy - hh, y1 = cy + hh;
		int red = (int) (25 + 230 * progress);
		int green = (int) (252 - 227 * progress);
		int fill = (100 << 24) | (red << 16) | (green << 8) | 25;
		int line = (255 << 24) | (red << 16) | (green << 8) | 25;
		if (!shapeMode.is("Lines")) {
			Render2D.rect(g, x0, y0, x1, y1, fill);
		}
		if (!shapeMode.is("Sides")) {
			Render2D.rect(g, x0, y0, x1, y0 + 1.5F, line);
			Render2D.rect(g, x0, y1 - 1.5F, x1, y1, line);
			Render2D.rect(g, x0, y0, x0 + 1.5F, y1, line);
			Render2D.rect(g, x1 - 1.5F, y0, x1, y1, line);
		}
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
