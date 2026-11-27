package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.Render2D;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/** 2D tracers from the crosshair toward nearby players. */
public class Tracers extends Module {
	public Tracers() {
		super("Tracers", 0, Category.RENDER);
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		float centerX = event.graphics.guiWidth() / 2f;
		float centerY = event.graphics.guiHeight() / 2f;
		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof net.minecraft.world.entity.player.Player) || entity == mc.player) {
				continue;
			}
			if (mc.player.distanceTo(entity) > 64) {
				continue;
			}
			Vec3 projected = mc.gameRenderer.projectPointToScreen(entity.position().add(0, entity.getBbHeight() * 0.5, 0));
			if (projected.z < -1 || projected.z > 1) {
				continue;
			}
			float x = (float) ((projected.x * 0.5 + 0.5) * event.graphics.guiWidth());
			float y = (float) ((-projected.y * 0.5 + 0.5) * event.graphics.guiHeight());
			Render2D.line(event.graphics, centerX, centerY, x, y, 1.0f, 0x66FF5555);
		}
	}
}
