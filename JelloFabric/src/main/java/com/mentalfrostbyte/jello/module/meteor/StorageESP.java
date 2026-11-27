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
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Meteor Client StorageESP: boxes around containers, coloured by type.
 * The shader mode is not ported (needs a post-process pipeline); everything is drawn as projected boxes.
 */
public class StorageESP extends Module {
	private record Found(BlockPos pos, int color) {
	}

	private final ModeValue shape = addValue(new ModeValue("Shape Mode", "Both", "Both", "Lines", "Sides"));
	private final NumberValue range = addValue(new NumberValue("Range", 48, 16, 128, 8));
	private final NumberValue fillOpacity = addValue(new NumberValue("Fill Opacity", 50, 0, 255, 5));
	private final BooleanValue tracers = addValue(new BooleanValue("Tracers", false));
	private final BooleanValue hideOpened = addValue(new BooleanValue("Hide Opened", false));

	private final List<Found> found = new ArrayList<>();
	private final Set<BlockPos> opened = new HashSet<>();
	private Screen lastScreen;
	private int timer;

	public StorageESP() {
		super("Storage Esp", 0, Category.RENDER);
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
		trackOpened();
		if (++timer < 40) {
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
		int fillAlpha = (int) fillOpacity.getValue() << 24;

		for (Found entry : found) {
			BlockPos pos = entry.pos();
			float[] box = screenBox(event, Vec3.atLowerCornerOf(pos), Vec3.atLowerCornerOf(pos).add(1, 1, 1));
			if (box == null) {
				continue;
			}
			int color = entry.color();
			if (sides) {
				Render2D.rect(event.graphics, box[0], box[1], box[2], box[3], (color & 0x00FFFFFF) | fillAlpha);
			}
			if (lines) {
				Render2D.rect(event.graphics, box[0], box[1], box[2], box[1] + 1.25f, color);
				Render2D.rect(event.graphics, box[0], box[3] - 1.25f, box[2], box[3], color);
				Render2D.rect(event.graphics, box[0], box[1], box[0] + 1.25f, box[3], color);
				Render2D.rect(event.graphics, box[2] - 1.25f, box[1], box[2], box[3], color);
			}
			if (tracers.getValue()) {
				Render2D.line(event.graphics, event.graphics.guiWidth() / 2f, event.graphics.guiHeight() / 2f,
					(box[0] + box[2]) / 2f, (box[1] + box[3]) / 2f, 1f, (color & 0x00FFFFFF) | 0x88000000);
			}
		}
	}

	private void scan() {
		found.clear();
		int radius = (int) range.getValue();
		int chunkRadius = Math.max(1, (int) Math.ceil(radius / 16.0));
		ChunkPos center = mc.player.chunkPosition();
		BlockPos playerPos = mc.player.blockPosition();
		int centerX = center.x();
		int centerZ = center.z();

		for (int cx = centerX - chunkRadius; cx <= centerX + chunkRadius; cx++) {
			for (int cz = centerZ - chunkRadius; cz <= centerZ + chunkRadius; cz++) {
				ChunkAccess access = mc.level.getChunk(cx, cz, ChunkStatus.FULL, false);
				if (!(access instanceof LevelChunk chunk)) {
					continue;
				}
				for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
					BlockPos pos = blockEntity.getBlockPos();
					if (playerPos.distSqr(pos) > (double) radius * radius) {
						continue;
					}
					int color = color(blockEntity);
					if (color != 0) {
						found.add(new Found(pos.immutable(), color));
					}
				}
			}
		}
	}

	private void trackOpened() {
		Screen current = mc.gui.screen();
		if (current == lastScreen) {
			return;
		}
		if (current != null && mc.hitResult instanceof BlockHitResult hit
			&& mc.level.getBlockEntity(hit.getBlockPos()) != null) {
			opened.add(hit.getBlockPos().immutable());
		}
		lastScreen = current;
	}

	private int color(BlockEntity blockEntity) {
		if (hideOpened.getValue() && opened.contains(blockEntity.getBlockPos())) {
			return 0;
		}
		if (blockEntity instanceof TrappedChestBlockEntity) return 0xFFFF0000;
		if (blockEntity instanceof EnderChestBlockEntity) return 0xFF7800FF;
		if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof BarrelBlockEntity
			|| blockEntity instanceof ShulkerBoxBlockEntity) return 0xFFFFA000;
		if (blockEntity instanceof AbstractFurnaceBlockEntity || blockEntity instanceof HopperBlockEntity
			|| blockEntity instanceof DispenserBlockEntity || blockEntity instanceof BrewingStandBlockEntity
			|| blockEntity instanceof CrafterBlockEntity || blockEntity instanceof DecoratedPotBlockEntity
			|| blockEntity instanceof ChiseledBookShelfBlockEntity) return 0xFF8C8C8C;
		return 0;
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
