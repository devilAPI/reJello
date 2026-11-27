package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.Render2D;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

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
			if (!(entity instanceof LivingEntity) || entity == mc.player) {
				continue;
			}
			if (mc.player.distanceTo(entity) > 64) {
				continue;
			}
			double dx = entity.getX() - mc.player.getX();
			double dz = entity.getZ() - mc.player.getZ();
			float angle = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
			float relative = angle - mc.player.getYRot();
			double radians = Math.toRadians(relative);
			float x = centerX + (float) Math.sin(radians) * 200;
			float y = centerY - (float) Math.cos(radians) * 120;
			Render2D.rect(event.graphics, centerX, centerY, x, y, 0x66FF5555);
		}
	}
}
