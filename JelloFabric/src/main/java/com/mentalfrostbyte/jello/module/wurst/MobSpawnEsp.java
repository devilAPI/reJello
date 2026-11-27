package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.Render2D;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

/**
 * Wurst MobSpawnESP: marks blocks where hostile mobs can spawn.
 *
 * <p>Jello has no 3D world render event, so the marks are drawn as projected 2D crosses like the
 * other ESP modules. Full port needs a world-render/vertex-buffer hook; the scan itself needs no
 * mixin.</p>
 */
public class MobSpawnEsp extends Module {
	private final NumberValue range = addValue(new NumberValue("Range", 16, 8, 32, 1));
	private final NumberValue opacity = addValue(new NumberValue("Opacity", 0.75, 0.1, 1, 0.05));
	private final BooleanValue showDay = addValue(new BooleanValue("Show Day Spots", false));

	private final List<BlockPos> found = new ArrayList<>();
	private int timer;

	public MobSpawnEsp() {
		super("Mob Spawn Esp", 0, Category.RENDER);
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
		if (++timer < 60) {
			return;
		}
		timer = 0;
		scan();
	}

	private void scan() {
		found.clear();
		int radius = (int) range.getValue();
		int yRadius = Math.min(12, radius / 2);
		BlockPos origin = mc.player.blockPosition();
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				for (int y = -yRadius; y <= yRadius; y++) {
					BlockPos pos = origin.offset(x, y, z);
					if (mc.level.getBrightness(LightLayer.BLOCK, pos) >= 1) {
						continue;
					}
					if (mc.level.getBrightness(LightLayer.SKY, pos) >= 8 && !showDay.getValue()) {
						continue;
					}
					if (SpawnPlacements.isSpawnPositionOk(EntityTypes.CREEPER, mc.level, pos)) {
						found.add(pos.immutable());
					}
				}
			}
		}
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || mc.player == null || mc.level == null || found.isEmpty()) {
			return;
		}
		int alpha = (int) (opacity.getValue() * 255);
		float guiW = event.graphics.guiWidth();
		float guiH = event.graphics.guiHeight();
		for (BlockPos pos : found) {
			Vec3 p = mc.gameRenderer.projectPointToScreen(Vec3.atCenterOf(pos).add(0, 0.51, 0));
			if (p.z < -1 || p.z > 1 || Double.isNaN(p.x) || Double.isNaN(p.y)) {
				continue;
			}
			float x = (float) ((p.x * 0.5 + 0.5) * guiW);
			float y = (float) ((-p.y * 0.5 + 0.5) * guiH);
			if (x < -8 || y < -8 || x > guiW + 8 || y > guiH + 8) {
				continue;
			}
			int rgb = mc.level.getBrightness(LightLayer.SKY, pos) < 8 ? 0xFFFF55 : 0xFF3333;
			int color = (alpha << 24) | rgb;
			Render2D.line(event.graphics, x - 3, y - 3, x + 3, y + 3, 1f, color);
			Render2D.line(event.graphics, x + 3, y - 3, x - 3, y + 3, 1f, color);
		}
	}
}
